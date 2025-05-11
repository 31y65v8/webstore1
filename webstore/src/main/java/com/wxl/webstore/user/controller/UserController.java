package com.wxl.webstore.user.controller;

import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.user.dto.UserLoginDTO;
import com.wxl.webstore.user.dto.UserRegisterDTO;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.common.enums.*;
import com.wxl.webstore.log.loginlog.service.LoginLogService;
import com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import com.wxl.webstore.common.response.Result;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wxl.webstore.user.dto.UserDTO;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-03-30
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private LoginLogService loginLogService;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody @Valid UserRegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserLoginDTO dto, HttpServletRequest request) {
        try {
            User user = userService.login(dto);
            // 生成JWT token返回给前端，使用登录时选择的角色
            String token = jwtUtil.generateToken(dto.getAccount(), user.getId(), dto.getRole());
            
            // 记录登录日志
            loginLogService.recordLoginLog(user.getId(), dto.getAccount(), dto.getRole(), request, true, "登录成功");
            
            return Result.success(token);
        } catch (Exception e) {
            // 记录登录失败日志 - 用户ID为空，因为登录失败无法获取用户ID
            loginLogService.recordLoginLog(null, dto.getAccount(), dto.getRole(), request, false, "登录失败：" + e.getMessage());
            throw e; // 继续抛出异常，让全局异常处理
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 解析token获取用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String account = jwtUtil.getAccountFromToken(token);
                UserRole role = jwtUtil.getUserRoleFromToken(token);
                
                // 记录退出日志
                loginLogService.recordLogoutLog(userId, account, role, request);
                
                // 由于JWT是无状态的，前端需要自行删除token
                return Result.success("退出登录成功");
            } catch (Exception e) {
                return Result.error(401, "无效的token: " + e.getMessage());
            }
        }
        return Result.error(401, "未授权的请求");
    }

    //注销账户，前端调用后应该自动接上退出登录操作！！！！！
    @PostMapping("/deleteuser")
    @OperationLogAnnoce(module = "用户模块", operation = "注销账户")
    public Result<User> deleteUserAccount(HttpServletRequest request) {
        // 从请求头获取token
        /*String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 解析token获取账号和角色
            String account = jwtUtil.getAccountFromToken(token);
            UserRole role = jwtUtil.getUserRoleFromToken(token);
            
            // 删除用户账号
            userService.deleteByAccountAndRole(account, role);
            return Result.success("账号删除成功");
        }
        return Result.error(401, "无效的token");*/
        try{
            Long userId = getUserIdFromToken(request);
        User user = userService.deleteAccount(userId);
        return Result.success(user);
        }catch (Exception e){
            return Result.error("注销失败：" + e.getMessage());
        }
    }

    @PostMapping("/deleteseller/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogAnnoce(module = "管理员模块", operation = "删除销售人员")
    public Result<User> deleteSellerAccount(@Parameter Long sellerId){

        try{
            //Long sellerId = getUserIdFromToken(request);
            User seller = userService.deleteAccount(sellerId);
            return Result.success(seller);
        }catch (Exception e){
            return Result.error("删除销售人员失败：" + e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 从token中获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                
                // 获取用户实体
                User user = userService.getUserInfo(userId);
                
                // 将实体转换为DTO
                UserDTO userDTO = UserDTO.fromEntity(user);
                
                // 返回DTO而不是实体
                return Result.success(userDTO);
            } catch (RuntimeException e) {
                // 捕获RuntimeException，包括token过期的异常
                if (e.getMessage() != null && e.getMessage().contains("Token已过期")) {
                    return Result.error(401, "Token已过期，请重新登录");
                }
                // 其他JWT异常
                return Result.error(401, "无效的token: " + e.getMessage());
            }
        }
        return Result.error(401, "未授权的请求");
    }

    @GetMapping("/allsellers")
    public Result<List<UserDTO>> getAllSellers(){
        List<User> sellers= userService.getAllSellers();
        List<UserDTO> sellerDTOs = UserDTO.fromEntity(sellers);
       return Result.success(sellerDTOs);
    }
}