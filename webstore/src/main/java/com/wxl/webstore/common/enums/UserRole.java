package com.wxl.webstore.common.enums;

import lombok.Getter;
import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    CUSTOMER("CUSTOMER", "顾客"),
    SELLER("SELLER", "商家"),
    ADMIN("ADMIN", "管理员");

    @EnumValue
    private final String value;
    private final String desc;

    UserRole(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.value;
    }
}