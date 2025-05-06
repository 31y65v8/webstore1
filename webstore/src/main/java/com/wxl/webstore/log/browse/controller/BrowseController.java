package com.wxl.webstore.log.browse.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.log.browse.entity.Browse;
import com.wxl.webstore.log.browse.service.BrowseService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 商品浏览记录表 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/api/browse")
public class BrowseController {
    @Autowired
    private BrowseService browseService;

    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping("/record")
    public Result<Void> recordBrowse(@RequestParam Long productId, HttpServletRequest request,@RequestParam Integer duration,@RequestParam String clickTime) {
        Long userId = getUserIdFromToken(request);
        browseService.recordBrowse(productId, userId,duration,clickTime);
        return Result.success(null);
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            System.out.println("从token中解析到的userId: " + userId);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败：" + e.getMessage());
        }
    }
}
