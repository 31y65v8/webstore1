package com.wxl.webstore.user.controller;

import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.user.dto.UserLoginDTO;
import com.wxl.webstore.user.dto.UserRegisterDTO;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.common.enums.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import com.wxl.webstore.common.Result;
import io.jsonwebtoken.Claims;
import java.util.HashMap;
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

    @PostMapping("/register")
    public Result<User> register(@RequestBody @Valid UserRegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid UserLoginDTO dto, HttpServletRequest request) {
        User user = userService.login(dto);
        // 生成JWT token返回给前端，使用登录时选择的角色
        String token = jwtUtil.generateToken(dto.getAccount(), user.getId(), dto.getRole());
        return Result.success(token);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 由于JWT是无状态的，前端需要自行删除token
        return Result.success("退出登录成功");
    }

    //注销账户，前端调用后应该自动接上退出登录操作！！！！！
    @DeleteMapping("/delete")
    public Result<String> deleteAccount(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 解析token获取账号和角色
            String account = jwtUtil.getAccountFromToken(token);
            UserRole role = jwtUtil.getUserRoleFromToken(token);
            
            // 删除用户账号
            userService.deleteByAccountAndRole(account, role);
            return Result.success("账号删除成功");
        }
        return Result.error(401, "无效的token");
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
}