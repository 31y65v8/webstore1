package com.wxl.webstore.SalesReport.dto;

import java.math.BigDecimal;

import com.wxl.webstore.common.enums.ProductCategory;

import java.io.Serializable;
import lombok.Data;

@Data
public class SellerCategorySalesReportDTO implements Serializable {
    private Long sellerId;
    private ProductCategory category;
    private Integer totalQuantity;
    private BigDecimal totalSalesAmount;
    // 可扩展更多字段
}
