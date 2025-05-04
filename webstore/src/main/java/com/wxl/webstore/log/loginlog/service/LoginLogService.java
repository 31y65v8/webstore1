package com.wxl.webstore.log.loginlog.service;

import com.wxl.webstore.log.loginlog.entity.LoginLog;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.common.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户登录登出日志表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
public interface LoginLogService extends IService<LoginLog> {
    /**
     * 记录用户登录日志
     * @param userId 用户ID
     * @param account 账号
     * @param role 角色
     * @param request HTTP请求对象
     * @param status 登录状态：true-成功 false-失败
     * @param message 消息
     */
    void recordLoginLog(Long userId, String account, UserRole role, HttpServletRequest request, boolean status, String message);

    /**
     * 记录用户退出日志
     * @param userId 用户ID
     * @param account 账号
     * @param role 角色
     * @param request HTTP请求对象
     */
    void recordLogoutLog(Long userId, String account, UserRole role, HttpServletRequest request);

    LocalDateTime getLastLoginTime(Long userId);
}
