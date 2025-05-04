package com.wxl.webstore.log.operationlog.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wxl.webstore.common.enums.UserRole;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
    * 操作日志表
    */
@Schema(description = "操作日志表")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("operation_log")
public class OperationLog implements Serializable {
private static final long serialVersionUID = 1L;

        @Schema(description = "日志ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
        @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "用户账号")
    @TableField("account")
    private String account;
        @Schema(description = "用户角色(SELLER/ADMIN)")
    @TableField("role")
    private UserRole role;
        @Schema(description = "操作模块")
    @TableField("module")
    private String module;
        @Schema(description = "操作内容")
    @TableField("operation")
    private String operation;
        @Schema(description = "请求URL")
    @TableField("request_url")
    private String requestUrl;
        @Schema(description = "请求方法")
    @TableField("request_method")
    private String requestMethod;
        @Schema(description = "请求参数")
    @TableField("request_params")
    private String requestParams;
        @Schema(description = "IP地址")
    @TableField("ip_address")
    private String ipAddress;
        @Schema(description = "操作时间")
    @TableField("operation_time")
    private LocalDateTime operationTime;
        @Schema(description = "用户代理信息")
    @TableField("user_agent")
    private String userAgent;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}