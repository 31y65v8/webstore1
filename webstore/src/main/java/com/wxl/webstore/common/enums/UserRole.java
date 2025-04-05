package com.wxl.webstore.common.enums;

import lombok.Getter;
import com.baomidou.mybatisplus.annotation.EnumValue;
/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    @EnumValue
    ADMIN("ADMIN", "管理员"),
    SELLER("SELLER", "商家"),
    CUSTOMER("CUSTOMER", "顾客");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static UserRole getByCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}