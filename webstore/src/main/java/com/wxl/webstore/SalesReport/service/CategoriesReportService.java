package com.wxl.webstore.SalesReport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.mapper.ProductMapper;
import com.wxl.webstore.SalesReport.dto.CategoriesSalesReportDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriesReportService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PurchaseLogService purchaseLogService;

    public List<CategoriesSalesReportDTO> getCategorySalesReport() {
        // 1. 查询所有类别
        List<String> categories = productMapper.selectAllCategories();

        List<CategoriesSalesReportDTO> reportList = new ArrayList<>();
        for (String category : categories) {
            // 2. 查询该类别所有商品ID
            List<Long> productIds = productMapper.selectIdsByCategory(category);

            // 3. 查询销量和销售额
            List<PurchaseLog> logs = purchaseLogService.list(
                new LambdaQueryWrapper<PurchaseLog>().in(PurchaseLog::getProductId, productIds)
            );
            int totalQuantity = logs.stream().mapToInt(PurchaseLog::getQuantity).sum();
            BigDecimal totalSales = logs.stream()
                .map(log -> log.getUnitPrice().multiply(BigDecimal.valueOf(log.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4. 查询库存和商品数
            List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>().in(Product::getId, productIds)
            );
            int productCount = products.size();
            int totalStock = products.stream().mapToInt(Product::getStorage).sum();
            int soldOutCount = (int) products.stream().filter(p -> p.getStorage() == 0).count();

            // 5. 组装DTO
            CategoriesSalesReportDTO dto = new CategoriesSalesReportDTO();
            dto.setCategory(category);
            dto.setTotalQuantity(totalQuantity);
            dto.setTotalSales(totalSales);
            dto.setProductCount(productCount);
            dto.setTotalStock(totalStock);
            dto.setSoldOutCount(soldOutCount);
            reportList.add(dto);
        }
        return reportList;
    }
}
