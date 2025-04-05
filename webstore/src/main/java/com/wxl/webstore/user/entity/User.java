package com.wxl.webstore.user.entity;

import com.wxl.webstore.common.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户实体")
@Getter
@Setter
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @Schema(description = "用户角色")
    @TableField("role")
    @EnumValue
    private UserRole role;

    @Schema(description = "注册手机号（唯一）")
    @TableField("register_phone")
    private String registerPhone;

    @TableField("register_email")
    private String registerEmail;

    @TableField("register_time")
    private LocalDateTime registerTime;

    @Schema(description = "是否删除(0-未删除,1-已删除)")
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Schema(description = "删除时间")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}