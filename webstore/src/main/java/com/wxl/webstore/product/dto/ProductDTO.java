package com.wxl.webstore.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.wxl.webstore.product.entity.Product;

@Data
public class ProductDTO {
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
    private String productDescription;
    private String productCategory;
    private Integer productStorage;
    
    
    public ProductDTO(Product product) {
        this.productId = String.valueOf(product.getId());
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.productImage = product.getImgurl();
        this.productDescription = product.getDescription();
        this.productCategory = product.getCategory().toString();
        this.productStorage = product.getStorage();
    }
}
