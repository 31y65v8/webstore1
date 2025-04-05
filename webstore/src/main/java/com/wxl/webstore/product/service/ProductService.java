package com.wxl.webstore.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品信息表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-01
 */
public interface ProductService extends IService<Product> {
    Page<Product> getPageOfProducts(int pageNum, int pageSize);
    Page<Product> getPageByCategory(int pageNum, int pageSize, ProductCategory category);
    Page<Product> getProductsByName(int pageNum, int pageSize, String name);
    List<Product> getProductsByIds(List<Long> productIds);

}
