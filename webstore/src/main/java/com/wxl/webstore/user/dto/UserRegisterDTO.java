package com.wxl.webstore.user.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import com.wxl.webstore.common.enums.UserRole;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度4-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    //@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    //private String phone;

    //@Email(message = "邮箱格式不正确")
    //private String email;

    @NotBlank(message = "账号不能为空")
    private String account;  // 手机号或邮箱


    // 选择注册角色,每个手机号/邮箱在每个角色上最多注册一次
    @NotNull(message = "请选择角色")
    private UserRole role;  // 角色字段，用户选择 SELLER 或 CUSTOMER

    // 用于验证account是手机号还是邮箱
    @AssertTrue(message = "账号必须是有效的手机号或邮箱")
    private boolean isValidAccount() {
        if (account == null || account.isEmpty()) {
            return false;
        }
        // 手机号格式验证
        boolean isPhone = account.matches("^1[3-9]\\d{9}$");
        // 邮箱格式验证
        boolean isEmail = account.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        return isPhone || isEmail;
    }


}