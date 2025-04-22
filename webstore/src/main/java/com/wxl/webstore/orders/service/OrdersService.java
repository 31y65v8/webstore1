package com.wxl.webstore.orders.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;

import jakarta.servlet.http.HttpServletRequest;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
public interface OrdersService extends IService<Orders> {
    void createOrderFromCart(Long userId, BigDecimal frontEndTotalPrice);//从购物车创建订单
    void cancelOrder(Long orderId, String cancelReason);//软删除取消订单（未付款时可以取消，付款后不能取消）
    void updateOrderStatus(Long orderId, OrderStatus newStatus);//更新订单状态
    //发货时给用户发短信或者邮件
    List<Orders> getOrdersByUserId(Long userId);//根据用户id获取所有订单
    Orders getOrderById(Long orderId);//根据订单id获取订单
    List<OrderItem> getOrderItemsByOrderId(Long orderId);//根据订单id获取所有订单项
    List<Orders> findExpiredPendingOrders(int minutes);
    void cancelOrder(Long orderId);
}
