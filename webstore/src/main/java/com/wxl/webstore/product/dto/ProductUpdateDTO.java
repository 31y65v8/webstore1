package com.wxl.webstore.product.dto;

import java.math.BigDecimal;

import com.wxl.webstore.common.enums.ProductCategory;
import lombok.Data;

@Data
public class ProductUpdateDTO {
    private String name;
    private BigDecimal price;
    private Integer storage;
    private ProductCategory category;
    private String description;
    private String imgurl;
}
