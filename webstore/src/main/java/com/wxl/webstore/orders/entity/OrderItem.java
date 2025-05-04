package com.wxl.webstore.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wxl.webstore.common.enums.OrderItemStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "")
@Getter
@Setter
@TableName("order_item")
public class OrderItem implements Serializable {
private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;
    @TableField("order_id")
    private Long orderId;
    @TableField("product_id")
    private Long productId;
    @TableField("product_name")
    private String productName;
    @TableField("product_price")
    private BigDecimal productPrice;
    @TableField("quantity")
    private Integer quantity;
    @TableField("total_price")
    private BigDecimal totalPrice;
    @TableField("status")
    private OrderItemStatus status;
}