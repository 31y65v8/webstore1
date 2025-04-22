package com.wxl.webstore.orders.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;


@Data
public class OrdersDTO {
    private Long id;
    private Long userId;
    private Long receiverId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
