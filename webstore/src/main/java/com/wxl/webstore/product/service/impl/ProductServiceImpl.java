package com.wxl.webstore.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.SalesReport.dto.SellerCategorySalesReportDTO;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.dto.ProductUpdateDTO;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.dto.ProductDTO;
import com.wxl.webstore.product.mapper.ProductMapper;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.ArrayList;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-01
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private PurchaseLogService purchaseLogService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 获取首页的分页商品
    @Override
    @Transactional
    public Page<Product> getPageOfProducts(int pageNum, int pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        //return productMapper.selectPageAll(page);
        // 创建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Product::getCreateTime); // 按创建时间降序

        return this.page(page, queryWrapper);
    }

    // 获取分类分页商品
    @Override
    @Transactional
    public Page<Product> getPageByCategory(int pageNum, int pageSize, ProductCategory category) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        //return productMapper.selectPageByCategory(page, category);
        // 创建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCategory, category); // 查询指定类别的商品
        queryWrapper.orderByDesc(Product::getCreateTime); // 按创建时间降序

        return this.page(page, queryWrapper);
    }

    // 根据商品名称模糊查询商品并分页
    @Override
    @Transactional
    public Page<Product> getProductsByName(int pageNum, int pageSize, String name) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        //return productMapper.selectPageByName(page, "%" + name + "%");  // "%" 符号用于 LIKE 查询
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();//省略了MySQL语句的编写
        queryWrapper.like(Product::getName, name); // 模糊匹配商品名称
        queryWrapper.orderByDesc(Product::getCreateTime); // 按创建时间降序排序

        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public List<Product> getProductsByIds(List<Long> productIds) {
        return (productIds == null || productIds.isEmpty())
                ? List.of()
                : productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .in(Product::getId, productIds)
        );
    }

    @Override
    @Transactional
    public Product addProduct(Product product) {
        // 验证商品基本信息
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }
        if (product.getStorage() == null) {
            throw new IllegalArgumentException("商品库存不能为空");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("商品分类不能为空");
        }
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("商品描述不能为空");
        }
        if (product.getImgurl() == null || product.getImgurl().trim().isEmpty()) {
            throw new IllegalArgumentException("商品图片不能为空");
        }

        // 从token中获取当前登录的卖家ID
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long sellerId = jwtUtil.getUserIdFromToken(token);
        
        // 设置商品基本信息
        product.setSellerId(sellerId);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        product.setIsDeleted(false);
        
        // 保存商品
        this.save(product);
        
        return product;
    }

    @Override
    @Transactional
    public Page<Product> getSellerProducts(int pageNum, int pageSize) {
        // 从token中获取当前登录的卖家ID
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long sellerId = jwtUtil.getUserIdFromToken(token);
        
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getSellerId, sellerId)
                   .eq(Product::getIsDeleted, false)
                   .orderByDesc(Product::getCreateTime);
        
        // 执行查询
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        // 获取当前登录的卖家ID
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long sellerId = jwtUtil.getUserIdFromToken(token);
        
        // 验证商品是否存在且属于当前卖家
        Product existingProduct = this.getProductById(product.getId());
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!existingProduct.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }

        // 更新商品信息
        product.setSellerId(sellerId); // 确保卖家ID不变
        product.setUpdateTime(LocalDateTime.now());
        product.setIsDeleted(false); // 确保不会被意外删除
        
        this.updateById(product);
        return product;
    }

    @Override
    @Transactional
    public Product updateProductPartial(Long id, ProductUpdateDTO dto) {
        Product existing = getById(id);
        if (existing == null) {
        throw new RuntimeException("商品不存在");
    }

    // 只更新非空字段
    if (dto.getName() != null) {
        existing.setName(dto.getName());
    }
    if (dto.getPrice() != null) {
        if (dto.getPrice().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("价格不能小于0");
        existing.setPrice(dto.getPrice());
    }
    if (dto.getStorage() != null) {
        if (dto.getStorage() < 0) throw new IllegalArgumentException("库存不能小于0");
        existing.setStorage(dto.getStorage());
    }
    if (dto.getCategory() != null) {
        existing.setCategory(dto.getCategory());
    }
    if (dto.getDescription() != null) {
        existing.setDescription(dto.getDescription());
    }
    if (dto.getImgurl() != null) {
        existing.setImgurl(dto.getImgurl());
    }

    updateById(existing);
    return existing;
}


    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 获取当前登录的卖家ID
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        Long sellerId = jwtUtil.getUserIdFromToken(token);
        
        // 验证商品是否存在且属于当前卖家
        Product product = this.getProductById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权删除此商品");
        }

        // 软删除商品
        product.setIsDeleted(true);
        product.setUpdateTime(LocalDateTime.now());
        this.updateById(product);
    }

    @Override
    @Transactional
    public Product getProductById(Long productId) {
        // 只按productId查询，不要加seller_id条件
        return productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getId, productId)
                .eq(Product::getIsDeleted, false));
    }

    @Override
    public String getProductNameById(Long id) {
        Product product = this.getProductById(id);
        return product.getName();
    }

    @Override
    public BigDecimal getProductPriceById(Long id) {
        Product product = this.getProductById(id);
        return product.getPrice();
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        String lockKey = "product_stock_lock:" + productId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间30秒
            boolean isLocked = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("获取商品锁失败，请稍后重试");
            }
            
            // 重新查询当前库存，确保数据最新
            Product product = this.getProductById(productId);
            
            // 检查库存是否充足
            if (product.getStorage() < quantity) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 更新库存
            int newStock = product.getStorage() - quantity;
            
            // 使用UpdateWrapper只更新storage字段
            UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", productId)
                         .set("storage", newStock);
            
            this.update(updateWrapper);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("减少库存操作被中断", e);
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        String lockKey = "product_stock_lock:" + productId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间30秒
            boolean isLocked = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("获取商品锁失败，请稍后重试");
            }
            
            // 重新查询当前库存，确保数据最新
            Product product = this.getProductById(productId);
            
            // 更新库存
            int newStock = product.getStorage() + quantity;
            
            // 使用UpdateWrapper只更新storage字段
            UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", productId)
                         .set("storage", newStock);
            
            this.update(updateWrapper);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("增加库存操作被中断", e);
        } finally {
            // 确保释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional
    public Page<ProductDTO> getPageOfProductDTOs(int pageNum, int pageSize) {
        // 获取原始商品分页
        Page<Product> productPage = getPageOfProducts(pageNum, pageSize);
        
        // 转换为DTO分页
        return convertToProductDTOPage(productPage);
    }

    @Override
    @Transactional
    public Page<ProductDTO> getPageByCategoryDTOs(int pageNum, int pageSize, ProductCategory category) {
        Page<Product> productPage = getPageByCategory(pageNum, pageSize, category);
        return convertToProductDTOPage(productPage);
    }

    @Override
    @Transactional
    public Page<ProductDTO> getProductsByNameDTOs(int pageNum, int pageSize, String name) {
        Page<Product> productPage = getProductsByName(pageNum, pageSize, name);
        return convertToProductDTOPage(productPage);
    }

    @Override
    @Transactional
    public List<ProductDTO> getProductsByIdsDTOs(List<Long> productIds) {
        List<Product> products = getProductsByIds(productIds);
        return products.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // 辅助方法：将 Product 分页转换为 ProductDTO 分页
    private Page<ProductDTO> convertToProductDTOPage(Page<Product> productPage) {
        Page<ProductDTO> dtoPage = new Page<>(
                productPage.getCurrent(),
                productPage.getSize(),
                productPage.getTotal());
        
        List<ProductDTO> dtoList = productPage.getRecords().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Transactional
    public void increaseSales(Long productId, int quantity) {
        
        // 验证商品是否存在且属于当前卖家
        Product product = this.getProductById(productId);   
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 增加销量
        int newSales = product.getSales() + quantity;
        product.setSales(newSales);
        this.updateById(product);
    }

    @Override
    public List<Long> getRecommendedProductsByCategoryAndPriceRange(ProductCategory category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.selectProductIdsByCategoryAndPriceRange(category.name(), minPrice, maxPrice);
    }

    @Override
    public Set<ProductCategory> getProductCategoriesBySellerId(Long sellerId) {
        List<Product> products = this.lambdaQuery()
            .eq(Product::getSellerId, sellerId)
            .select(Product::getCategory)
            .list();
        // 提取品类去重
        return products.stream()
            .map(Product::getCategory)
            .collect(Collectors.toSet());
    }

    // 查询销售人员各品类销售业绩
    public List<SellerCategorySalesReportDTO> getSellerCategorySalesReport(Long sellerId) {
        Set<ProductCategory> categories = getProductCategoriesBySellerId(sellerId);
        List<SellerCategorySalesReportDTO> reportList = new ArrayList<>();
        for (ProductCategory category : categories) {
            // 查询该销售人员该品类下所有商品ID
            List<Long> productIds = this.lambdaQuery()
                .eq(Product::getSellerId, sellerId)
                .eq(Product::getCategory, category)
                .select(Product::getId)
                .list()
                .stream().map(Product::getId).collect(Collectors.toList());
            if (productIds.isEmpty()) continue;

            // 查询这些商品的销售日志
            List<PurchaseLog> logs = purchaseLogService.list(
                new LambdaQueryWrapper<PurchaseLog>().in(PurchaseLog::getProductId, productIds)
            );
            int totalQuantity = logs.stream().mapToInt(PurchaseLog::getQuantity).sum();
            BigDecimal totalAmount = logs.stream()
                .map(log -> log.getUnitPrice().multiply(BigDecimal.valueOf(log.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            SellerCategorySalesReportDTO dto = new SellerCategorySalesReportDTO();
            dto.setSellerId(sellerId);
            dto.setCategory(category);
            dto.setTotalQuantity(totalQuantity);
            dto.setTotalSalesAmount(totalAmount);
            reportList.add(dto);
        }
        return reportList;
    }

    // 每天凌晨1点定时统计并缓存，保留24小时
    @Scheduled(cron = "0 0 1 * * ?")
    public void cacheAllSellerCategorySalesReports() {
        // 获取所有销售人员ID
        List<Long> sellerIds = this.baseMapper.selectDistinctSellerIds();
        for (Long sellerId : sellerIds) {
            List<SellerCategorySalesReportDTO> report = getSellerCategorySalesReport(sellerId);
            String cacheKey = "seller:category:sales:report:" + sellerId;
            redisTemplate.opsForValue().set(cacheKey, report, 24, TimeUnit.HOURS);
        }
    }

    // 查询缓存
    public List<SellerCategorySalesReportDTO> getCachedSellerCategorySalesReport(Long sellerId) {
        String cacheKey = "seller:category:sales:report:" + sellerId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (List<SellerCategorySalesReportDTO>) cached;
        }
        // 缓存没有则实时查
        return getSellerCategorySalesReport(sellerId);
    }
}
