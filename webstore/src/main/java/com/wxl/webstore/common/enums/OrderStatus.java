package com.wxl.webstore.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "待支付"),
    PAID("PAID", "已支付"),
    CANCELLED("CANCELLED", "已取消");
    @EnumValue
    private final String code;

    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
