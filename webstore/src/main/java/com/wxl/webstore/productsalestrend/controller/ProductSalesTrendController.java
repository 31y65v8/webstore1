package com.wxl.webstore.productsalestrend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wxl.webstore.productsalestrend.dto.ProductSalesTrendDTO;
import com.wxl.webstore.productsalestrend.service.ProductSalesTrendService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
public class ProductSalesTrendController {

    @Autowired
    ProductSalesTrendService productSalesTrendService;
    
    @GetMapping("/product-sales-trend/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void getProductTrends(@RequestParam String productId){
        Long productIdLong = Long.parseLong(productId);
        productSalesTrendService.getProductSalesTrend(productIdLong);
    }

    @GetMapping("/product-sales-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductSalesTrendDTO> getAllProductTrends(){
        return productSalesTrendService.getAllProductTrends();
    }


}
