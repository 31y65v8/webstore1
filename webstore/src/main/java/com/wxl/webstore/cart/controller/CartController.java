package com.wxl.webstore.cart.controller;

import com.wxl.webstore.cart.dto.CartDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.wxl.webstore.cart.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import com.wxl.webstore.common.utils.JwtUtil;

/**
 * <p>
 * 用户购物车表 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取购物车商品列表
    @GetMapping("/items")
    public List<CartDTO> getCartItems(HttpServletRequest request) {
        return cartService.getCartItems(request);
    }

    // 添加商品到购物车
    @PostMapping("/add")
    public void addToCart(@RequestParam String productId,
                          @RequestParam Integer quantity,
                          HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            // 将字符串ID转换为Long
            Long productIdLong = Long.parseLong(productId);
            cartService.addToCart(userId, productIdLong, quantity);
        } catch (Exception e) {
            throw new RuntimeException("添加购物车失败: " + e.getMessage());
        }
    }

    // 删除购物车中的商品
    @DeleteMapping("/remove")
    public void removeFromCart(HttpServletRequest request,
                               @RequestParam Long productId) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.removeFromCart(userId, productId);
        } catch (Exception e) {
            throw new RuntimeException("删除购物车失败: " + e.getMessage());
        }
    }

    // 更新购物车中商品的数量
    @PutMapping("/update")
    public void updateQuantity( HttpServletRequest request,
                               @RequestParam Long productId,
                               @RequestParam Integer quantity) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.updateQuantity(userId, productId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("更新购物车失败: " + e.getMessage());
        }
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public void clearCart(  HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.clearCart(userId);
        } catch (Exception e) {
            throw new RuntimeException("清空购物车失败: " + e.getMessage());
        }
    }

    //选择购物车中的商品
    @PutMapping("/select")
    public void selectProduct(HttpServletRequest request,
                             @RequestParam Long productId,
                             @RequestParam Boolean selected) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.selectProduct(userId, productId, selected);
        } catch (Exception e) {
            throw new RuntimeException("选择商品失败: " + e.getMessage());
        }
    }

    //全选购物车
    @PutMapping("/selectAll")
    public void selectAll(HttpServletRequest request,
                         @RequestParam Boolean selected) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.selectAll(userId, selected);
        } catch (Exception e) {
            throw new RuntimeException("选择全选失败: " + e.getMessage());
        }
    }
    
    //取消全选
    @PutMapping("/unselectAll")
    public void unselectAll(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            cartService.unselectAll(userId);
        } catch (Exception e) {
            throw new RuntimeException("取消全选失败: " + e.getMessage());
        }
    }


}
