package com.wxl.webstore.orders.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.mapper.OrderItemMapper;
import com.wxl.webstore.orders.service.OrderItemService;
import com.wxl.webstore.product.service.ProductService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void createOrderItem(List<OrderItem> orderItems) {
        this.saveBatch(orderItems);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        this.removeById(orderItemId);
    }

    @Override
    @Transactional
    public void updateOrderItemQuantity(Long orderItemId, int newQuantity) {
        // 验证参数
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        
        // 获取当前订单项
        OrderItem orderItem = this.getById(orderItemId);
        if (orderItem == null) {
            throw new RuntimeException("订单项不存在");
        }
        
        // 获取数量变化
        int quantityDifference = newQuantity - orderItem.getQuantity();
        
        // 如果数量没有变化，直接返回
        if (quantityDifference == 0) {
            return;
        }
        
        // 更新商品库存
        Long productId = orderItem.getProductId();
        if (quantityDifference > 0) {
            // 如果新数量大于旧数量，减少库存
            productService.decreaseStock(productId, quantityDifference);
        } else {
            // 如果新数量小于旧数量，增加库存
            productService.increaseStock(productId, -quantityDifference);
        }
        
        // 更新订单项数量和总价
        BigDecimal unitPrice = orderItem.getProductPrice();
        BigDecimal newTotalPrice = unitPrice.multiply(new BigDecimal(newQuantity));
        
        orderItem.setQuantity(newQuantity);
        orderItem.setTotalPrice(newTotalPrice);
        this.updateById(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return this.list(queryWrapper);
    }

    @Override
    public OrderItem getOrderItemById(Long orderItemId) {
        return this.getById(orderItemId);
    }

}
