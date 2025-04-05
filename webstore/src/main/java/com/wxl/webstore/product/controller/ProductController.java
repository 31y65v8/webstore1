package com.wxl.webstore.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * <p>
 * 商品信息表 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-01
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 获取首页商品分页数据
    @GetMapping("/products")
    public Page<Product> getProductsPage(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        return productService.getPageOfProducts(pageNum, pageSize);
    }

    // 获取按照分类分页显示商品
    @GetMapping("/products/category")
    public Page<Product> getProductsByCategory(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @RequestParam ProductCategory category) {
        return productService.getPageByCategory(pageNum, pageSize, category);
    }

    // 获取商品名称模糊搜索的分页结果
    @GetMapping("/products/search")
    public Page<Product> searchProductsByName(@RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam String name) {
        return productService.getProductsByName(pageNum, pageSize, name);
    }

}
