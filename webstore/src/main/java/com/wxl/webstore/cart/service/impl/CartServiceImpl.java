package com.wxl.webstore.cart.service.impl;

import com.wxl.webstore.cart.dto.CartDTO;
import com.wxl.webstore.cart.entity.Cart;
import com.wxl.webstore.cart.mapper.CartMapper;
import com.wxl.webstore.cart.service.CartService;
import com.wxl.webstore.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.common.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户购物车表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public List<CartDTO> getCartItems(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 查询购物车数据
        List<Cart> cartItems = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        // 获取所有 product_id
        List<Long> productIds = cartItems.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());

        // 通过 product_id 批量查询商品信息
        List<Product> products = productService.getProductsByIds(productIds);

        // 组装返回数据
        return cartItems.stream().map(cart -> {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(cart.getProductId()))
                    .findFirst().orElse(null);
            return new CartDTO(cart, product);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 查询购物车中是否已有该商品
        Cart existingCart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));

        if (existingCart != null) {
            // 商品已存在，则更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartMapper.updateById(existingCart);
        } else {
            // 新增购物车商品
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(productId);
            newCart.setQuantity(quantity);
            newCart.setSelected(false);
            cartMapper.insert(newCart);
        }

    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));

        if (cart != null) {
            cart.setQuantity(quantity);
            cartMapper.updateById(cart);
        }
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
    }

    @Override
    @Transactional
    public void selectProduct(Long userId, Long productId, Boolean selected) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));
        if (cart != null) {
            cart.setSelected(selected);
            cartMapper.updateById(cart);
        }
    }

    @Override
    @Transactional
    public void selectAll(Long userId, Boolean selected) {
        List<Cart> cartItems = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
        for (Cart cart : cartItems) {
            cart.setSelected(selected);
            cartMapper.updateById(cart);
        }
    }

    @Override
    @Transactional
    public void unselectAll(Long userId) {
        selectAll(userId, false);
    }

    @Override
    public List<Cart> getSelectedItems(Long userId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("selected", 1);
        return cartMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void clearSelectedItems(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getSelected, 1));
    }

}
