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
    //选择购物车中的商品
    void selectProduct(Long userId, Long productId, Boolean selected);
    //全选购物车
    void selectAll(Long userId, Boolean selected);
    //取消全选
    void unselectAll(Long userId);
    //获取购物车中选中的商品
    List<Cart> getSelectedItems(Long userId);
    //清除购物车中被选中的商品
    void clearSelectedItems(Long userId);
    //获取购物车中未选中的商品
    //List<CartDTO> getUnselectedProducts(Long userId);
    //获取购物车中选中的商品数量
    //Integer getSelectedProductCount(Long userId);
    

}
