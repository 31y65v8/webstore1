package com.wxl.webstore.log.loginlog.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import com.wxl.webstore.common.enums.UserRole;
import com.baomidou.mybatisplus.annotation.EnumValue;

    /**
    * 用户登录登出日志表
    */
@Schema(description = "用户登录登出日志表")
@Getter
@Setter
@TableName("login_log")
public class LoginLog implements Serializable {
private static final long serialVersionUID = 1L;

        @Schema(description = "日志ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
        @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "账号(手机号/邮箱)")
    @TableField("account")
    private String account;
        @Schema(description = "用户角色(CUSTOMER/SELLER/ADMIN)")
    @TableField("role")
    private UserRole role;
        @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;
        @Schema(description = "登录/退出时间")
    @TableField("login_time")
    private LocalDateTime loginTime;
        @Schema(description = "操作类型：1-登录 2-退出")
    @TableField("operation_type")
    private Byte operationType;
        @Schema(description = "操作状态：0-失败 1-成功")
    @TableField("status")
    private Byte status;
        @Schema(description = "操作消息")
    @TableField("message")
    private String message;
        @Schema(description = "用户代理信息(浏览器/设备信息)")
    @TableField("user_agent")
    private String userAgent;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}