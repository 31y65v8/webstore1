package com.wxl.webstore.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.mapper.ProductMapper;
import com.wxl.webstore.product.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 获取首页的分页商品
    @Override
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
    public Page<Product> getProductsByName(int pageNum, int pageSize, String name) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        //return productMapper.selectPageByName(page, "%" + name + "%");  // "%" 符号用于 LIKE 查询
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();//省略了MySQL语句的编写
        queryWrapper.like(Product::getName, name); // 模糊匹配商品名称
        queryWrapper.orderByDesc(Product::getCreateTime); // 按创建时间降序排序

        return this.page(page, queryWrapper);
    }

    @Override
    public List<Product> getProductsByIds(List<Long> productIds) {
        return (productIds == null || productIds.isEmpty())
                ? List.of()
                : productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .in(Product::getId, productIds)
        );
    }

}
