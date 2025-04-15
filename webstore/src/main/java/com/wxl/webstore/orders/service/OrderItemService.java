package com.wxl.webstore.orders.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.orders.entity.OrderItem;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
public interface OrderItemService extends IService<OrderItem> {
    void createOrderItem(List<OrderItem> orderItems);//创建订单项
    void deleteOrderItem(Long orderItemId);//真删除订单项
    void updateOrderItemQuantity(Long orderItemId, int quantity);//修改订单项数量
    List<OrderItem> getOrderItemsByOrderId(Long orderId);//根据订单id获取所有订单项
    OrderItem getOrderItemById(Long orderItemId);//根据订单项id获取订单项
}
