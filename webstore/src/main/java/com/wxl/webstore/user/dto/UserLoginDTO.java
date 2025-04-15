package com.wxl.webstore.user.dto;

import com.wxl.webstore.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "账号不能为空")
    private String account;  // 可以是手机号或邮箱

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "用户角色不能为空")
    private UserRole role;  // 登录时选择的角色
}
