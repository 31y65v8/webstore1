package com.wxl.webstore.log.loginlog.service.impl;

import com.wxl.webstore.log.loginlog.entity.LoginLog;
import com.wxl.webstore.log.loginlog.mapper.LoginLogMapper;
import com.wxl.webstore.log.loginlog.service.LoginLogService;
import com.wxl.webstore.common.utils.IpUtil;
import com.wxl.webstore.common.enums.UserRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户登录登出日志表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public void recordLoginLog(Long userId, String account, UserRole role, HttpServletRequest request, boolean status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setAccount(account);
        loginLog.setRole(role);
        loginLog.setIpAddress(IpUtil.getIpAddr(request));
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setOperationType((byte) 1); // 1-登录
        loginLog.setStatus(status ? (byte) 1 : (byte) 0); // 状态：1-成功 0-失败
        loginLog.setMessage(message);
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setCreateTime(LocalDateTime.now());
        save(loginLog);
    }

    @Override
    public void recordLogoutLog(Long userId, String account, UserRole role, HttpServletRequest request) {
        LoginLog logoutLog = new LoginLog();
        logoutLog.setUserId(userId);
        logoutLog.setAccount(account);
        logoutLog.setRole(role);
        logoutLog.setIpAddress(IpUtil.getIpAddr(request));
        logoutLog.setLoginTime(LocalDateTime.now());
        logoutLog.setOperationType((byte) 2); // 2-退出
        logoutLog.setStatus((byte) 1); // 成功
        logoutLog.setMessage("用户退出成功");
        logoutLog.setUserAgent(request.getHeader("User-Agent"));
        logoutLog.setCreateTime(LocalDateTime.now());
        save(logoutLog);
    }

    @Override
    public LocalDateTime getLastLoginTime(Long userId) {
        LoginLog lastLogin = this.getOne(new LambdaQueryWrapper<LoginLog>()
            .eq(LoginLog::getUserId, userId)
            .eq(LoginLog::getOperationType, (byte)1)  // 登录操作
            .eq(LoginLog::getStatus, (byte)1)         // 成功状态
            .orderByDesc(LoginLog::getLoginTime)
            .last("LIMIT 1"));
            
        return lastLogin != null ? lastLogin.getLoginTime() : null;
    }
}
