package com.wxl.webstore.SalesReport.controller;

import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.product.service.impl.ProductServiceImpl;
import com.wxl.webstore.SalesReport.dto.CategoriesSalesReportDTO;
import com.wxl.webstore.SalesReport.dto.SellerCategorySalesReportDTO;
import com.wxl.webstore.SalesReport.service.CategoriesReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class SalesReportController {

    @Autowired
    private ProductServiceImpl productServiceImpl;

    @Autowired
    private CategoriesReportService categoriesReportService;

    /**
     * 查询指定销售人员的品类销售业绩报表
     * @param sellerId 销售人员ID
     * @return 品类销售业绩列表
     */
    @GetMapping("/seller-category-report")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SellerCategorySalesReportDTO> getSellerCategoryReport(@RequestParam Long sellerId) {
        // 优先查缓存
        return productServiceImpl.getCachedSellerCategorySalesReport(sellerId);
    }

    @GetMapping("/category-sales-report")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoriesSalesReportDTO> getProductCategorySalesReport(){
        return categoriesReportService.getCategorySalesReport();
    }

  

    
}
