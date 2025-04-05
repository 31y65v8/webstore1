package com.wxl.webstore.cart.controller;

import com.wxl.webstore.cart.dto.CartDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.wxl.webstore.cart.service.CartService;

/**
 * <p>
 * 用户购物车表 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    // 获取购物车商品列表
    @GetMapping("/items")
    public List<CartDTO> getCartItems(@RequestParam Long userId) {
        return cartService.getCartItems(userId);
    }

    // 添加商品到购物车
    @PostMapping("/add")
    public void addToCart(@RequestParam Long userId,
                          @RequestParam Long productId,
                          @RequestParam Integer quantity) {
        cartService.addToCart(userId, productId, quantity);
    }

    // 删除购物车中的商品
    @DeleteMapping("/remove")
    public void removeFromCart(@RequestParam Long userId,
                               @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
    }

    // 更新购物车中商品的数量
    @PutMapping("/update")
    public void updateQuantity(@RequestParam Long userId,
                               @RequestParam Long productId,
                               @RequestParam Integer quantity) {
        cartService.updateQuantity(userId, productId, quantity);
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public void clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
    }



}
