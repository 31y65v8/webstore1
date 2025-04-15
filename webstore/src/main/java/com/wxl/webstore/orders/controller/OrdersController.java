package com.wxl.webstore.orders.controller;

import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;
import com.wxl.webstore.orders.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单管理 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 从购物车创建订单
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<Void> createOrderFromCart(@RequestParam Long userId) {
        try {
            ordersService.createOrderFromCart(userId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        try {
            ordersService.cancelOrder(orderId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("取消订单失败：" + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status/{orderId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public Result<Void> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestParam OrderStatus newStatus) {
        try {
            ordersService.updateOrderStatus(orderId, newStatus);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("更新订单状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户所有订单
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<List<Orders>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<Orders> orders = ordersService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public Result<Orders> getOrderById(@PathVariable Long orderId) {
        try {
            Orders order = ordersService.getOrderById(orderId);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("获取订单详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单的所有订单项
     */
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public Result<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderItem> orderItems = ordersService.getOrderItemsByOrderId(orderId);
            return Result.success(orderItems);
        } catch (Exception e) {
            return Result.error("获取订单项失败：" + e.getMessage());
        }
    }
}
