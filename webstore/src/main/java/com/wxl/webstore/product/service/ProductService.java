package com.wxl.webstore.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.product.dto.ProductDTO;
import com.wxl.webstore.product.dto.ProductUpdateDTO;
import com.wxl.webstore.product.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
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
    Product addProduct(Product product);
    Page<Product> getSellerProducts(int pageNum, int pageSize);
    Product updateProduct(Product product);//全部更新，用不上
    void deleteProduct(Long id);
    Product getProductById(Long id);
    Product updateProductPartial(Long id, ProductUpdateDTO dto);//部分更新
    String getProductNameById(Long id);
    BigDecimal getProductPriceById(Long id);
    void decreaseStock(Long productId, int quantity);//减少商品库存
    void increaseStock(Long productId, int quantity);//增加商品库存
    Page<ProductDTO> getPageOfProductDTOs(int pageNum, int pageSize);
    Page<ProductDTO> getPageByCategoryDTOs(int pageNum, int pageSize, ProductCategory category);
    Page<ProductDTO> getProductsByNameDTOs(int pageNum, int pageSize, String name);
    List<ProductDTO> getProductsByIdsDTOs(List<Long> productIds);
}
