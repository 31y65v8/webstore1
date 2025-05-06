package com.wxl.webstore.productsalestrend.dto;

import java.util.List;
import lombok.*;

@Data
public class ProductSalesTrendDTO {
    private Long productId;
    private String productName;
    private List<Integer> monthlySales; // 近12个月销量
    private Double nextMonthPrediction;
    private String trend; // 上升/下降/平稳
    private Integer currentMonthSales;
    private Boolean isAnomaly;
    
}
