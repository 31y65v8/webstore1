package com.wxl.webstore.orders.controller;

import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单项管理 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@RestController
@RequestMapping("/api/orderItems")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * 修改订单项数量
     */
    @PutMapping("/{orderItemId}/quantity")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<Void> updateOrderItemQuantity(
            @PathVariable Long orderItemId,
            @RequestParam int quantity) {
        try {
            orderItemService.updateOrderItemQuantity(orderItemId, quantity);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("更新订单项数量失败：" + e.getMessage());
        }
    }

    /**
     * 删除订单项
     */
    @DeleteMapping("/{orderItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        try {
            orderItemService.deleteOrderItem(orderItemId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除订单项失败：" + e.getMessage());
        }
    }

    /**
     * 根据订单ID获取所有订单项
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public Result<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
            return Result.success(orderItems);
        } catch (Exception e) {
            return Result.error("获取订单项失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单项详情
     */
    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public Result<OrderItem> getOrderItemById(@PathVariable Long orderItemId) {
        try {
            OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
            return Result.success(orderItem);
        } catch (Exception e) {
            return Result.error("获取订单项详情失败：" + e.getMessage());
        }
    }
}
