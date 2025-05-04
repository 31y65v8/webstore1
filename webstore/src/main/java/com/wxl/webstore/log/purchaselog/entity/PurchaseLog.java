package com.wxl.webstore.log.purchaselog.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wxl.webstore.common.enums.ProductCategory;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
    * 商品购买记录日志表
    */
@Schema(description = "商品购买记录日志表")
@Getter
@Setter
@TableName("purchase_log")
public class PurchaseLog implements Serializable {
private static final long serialVersionUID = 1L;

        @Schema(description = "日志ID")
    @TableField("id")
    private Long id;
        @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;
        @Schema(description = "商品ID")
    @TableField("product_id")
    private Long productId;
        @Schema(description = "商品类别")
    @TableField("product_category")
    private ProductCategory productCategory;
        @Schema(description = "购买数量")
    @TableField("quantity")
    private Integer quantity;
        @Schema(description = "商品单价")
    @TableField("unit_price")
    private BigDecimal unitPrice;
        @Schema(description = "购买时间")
    @TableField("purchase_time")
    private LocalDateTime purchaseTime;
        @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}