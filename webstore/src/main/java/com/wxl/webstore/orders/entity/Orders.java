package com.wxl.webstore.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wxl.webstore.common.enums.OrderStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "")
@Getter
@Setter
@TableName("orders")
public class Orders implements Serializable {
private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("receiver_id")
    private Long receiverId;
    @TableField("total_price")
    private BigDecimal totalPrice;
    @TableField("status")
    private OrderStatus status;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}