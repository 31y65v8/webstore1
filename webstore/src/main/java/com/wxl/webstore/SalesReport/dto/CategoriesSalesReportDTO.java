package com.wxl.webstore.SalesReport.dto;

import java.math.BigDecimal;

import com.wxl.webstore.common.enums.ProductCategory;

import lombok.*;

@Data
public class CategoriesSalesReportDTO {

    private String category;           // 商品类别
    private Integer totalQuantity;     // 销量
    private BigDecimal totalSales;     // 销售额
    private Integer productCount;      // 该类别商品数
    private Integer totalStock;        // 总库存
    private Integer soldOutCount;      // 已售罄商品数

}
