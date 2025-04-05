package com.wxl.webstore.cart.service;

import com.wxl.webstore.cart.dto.CartDTO;
import com.wxl.webstore.cart.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户购物车表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
public interface CartService extends IService<Cart> {
    //展示购物车
    List<CartDTO> getCartItems(Long userId);
    //加入购物车
    void addToCart(Long userId, Long productId, Integer quantity);
    //从购物车删除
    void removeFromCart(Long userId, Long productId);
    //更改购物车中某商品数量
    void updateQuantity(Long userId, Long productId, Integer quantity);
    //清空购物车
    void clearCart(Long userId);

}
