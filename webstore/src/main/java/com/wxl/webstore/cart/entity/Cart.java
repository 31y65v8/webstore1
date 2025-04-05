package com.wxl.webstore.cart.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
    * 用户购物车表
    */
@Schema(description = "用户购物车表")
@Getter
@Setter
@TableName("cart")
public class Cart implements Serializable {
private static final long serialVersionUID = 1L;

    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
        @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "商品ID")
    @TableField("product_id")
    private Long productId;
        @Schema(description = "购买数量")
    @TableField("quantity")
    private Integer quantity;
        @Schema(description = "是否选中")
    @TableField("selected")
    private Boolean selected;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
        @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}