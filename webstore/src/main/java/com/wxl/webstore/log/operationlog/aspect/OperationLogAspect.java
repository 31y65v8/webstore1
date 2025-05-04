package com.wxl.webstore.log.operationlog.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxl.webstore.common.enums.UserRole;
import com.wxl.webstore.common.utils.IpUtil;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce;
import com.wxl.webstore.log.operationlog.entity.OperationLog;
import com.wxl.webstore.log.operationlog.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce)")
    public void operationLogPointCut() {}
    
    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "operationLogPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }
    
    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获取注解
            OperationLogAnnoce controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }
            
            // 获取当前请求对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // 从请求头获取token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return;
            }
            token = token.substring(7);
            
            // 从token中获取用户信息
            Long userId;
            String account;
            String role;
            
            try {
                userId = jwtUtil.getUserIdFromToken(token);
                account = jwtUtil.getAccountFromToken(token);
                role = jwtUtil.getUserRoleFromToken(token).toString();
                
                // 如果不是SELLER或ADMIN，则不记录
                if (!role.equals("SELLER") && !role.equals("ADMIN")) {
                    return;
                }
            } catch (Exception tokenEx) {
                return;
            }
            
            // 获取请求的参数
            Map<String, Object> paramsMap = new HashMap<>();
            // 获取请求参数
            // 省略复杂的参数获取逻辑，根据实际项目情况处理
            String params = "{}";
            try {
                params = objectMapper.writeValueAsString(joinPoint.getArgs());
            } catch (Exception paramsEx) {
                // 忽略参数转换错误
            }
            
            OperationLog operLog = new OperationLog();
            operLog.setUserId(userId);
            operLog.setAccount(account);
            operLog.setRole(UserRole.valueOf(role));
            operLog.setModule(controllerLog.module());
            operLog.setOperation(controllerLog.operation());
            operLog.setRequestUrl(request.getRequestURI());
            operLog.setRequestMethod(request.getMethod());
            operLog.setRequestParams(params);
            operLog.setIpAddress(IpUtil.getIpAddr(request));
            operLog.setOperationTime(LocalDateTime.now());
            operLog.setUserAgent(request.getHeader("User-Agent"));
            operLog.setCreateTime(LocalDateTime.now());
            
            // 保存数据库
            operationLogService.saveOperationLog(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            exp.printStackTrace();
        }
    }
    
    /**
     * 获取注解对象
     */
    private OperationLogAnnoce getAnnotationLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        return method.getAnnotation(OperationLogAnnoce.class);
    }
}
