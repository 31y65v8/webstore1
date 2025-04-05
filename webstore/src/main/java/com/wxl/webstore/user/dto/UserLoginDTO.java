package com.wxl.webstore.user.dto;

import com.wxl.webstore.common.enums.UserRole;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserLoginDTO {
    @NotBlank(message = "账号不能为空,可以是手机/邮箱")
    private String account; // 可以是用户名/手机/邮箱

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "请选择角色")
    private UserRole role;
}
