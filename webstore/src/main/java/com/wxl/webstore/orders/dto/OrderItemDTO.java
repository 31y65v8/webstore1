package com.wxl.webstore.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private BigDecimal totalPrice;
    private Integer quantity;
    private String status;
    private Long userId;
    private LocalDateTime createTime;
    
    // 收货信息
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String addressDetail;
}
