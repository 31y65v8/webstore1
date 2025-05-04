package com.wxl.webstore.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderItemStatus {
    PENDING("PENDING", "待支付"),
    PAID("PAID", "已支付"),
    SHIPPED("SHIPPED", "已发货"),
    DELIVERED("DELIVERED", "已送达"),
    CANCELLED("CANCELLED"   , "已取消");

    @EnumValue
    private final String code;

    private final String description;

    OrderItemStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    
    
}
