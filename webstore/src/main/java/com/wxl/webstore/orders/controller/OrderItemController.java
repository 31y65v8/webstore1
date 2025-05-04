package com.wxl.webstore.orders.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.common.enums.OrderItemStatus;
import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce;
import com.wxl.webstore.orders.dto.OrderItemDTO;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.service.OrderItemService;
import com.wxl.webstore.common.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

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

    @Autowired
    private JwtUtil jwtUtil;

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

    /**
     * 卖家获取待发货订单项列表
     */
    @GetMapping("/seller/paid")
    @PreAuthorize("hasRole('SELLER')")
    @OperationLogAnnoce(module = "订单模块", operation = "查看待发货订单项列表")
    public Result<Page<OrderItemDTO>> getSellerPaidOrderItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long sellerId = getUserIdFromToken(request);
            Page<OrderItemDTO> orderItems = orderItemService.getSellerPendingOrderItems(sellerId, page, size);
            return Result.success(orderItems);
        } catch (Exception e) {
            return Result.error("获取待发货订单失败：" + e.getMessage());
        }
    }

    /**
     * 卖家获取待发货订单数量
     */
    @GetMapping("/seller/paid/count")
    @PreAuthorize("hasRole('SELLER')")
    public Result<Integer> getSellerPendingOrderItemsCount(HttpServletRequest request) {
        try {
            Long sellerId = getUserIdFromToken(request);
            int count = orderItemService.getSellerPendingOrderItemsCount(sellerId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取待发货订单数量失败：" + e.getMessage());
        }
    }

    /**
     * 卖家发货
     */
    @PostMapping("/seller/{orderItemId}/ship")
    @PreAuthorize("hasRole('SELLER')")
    @OperationLogAnnoce(module = "订单模块", operation = "更新订单项状态为发货")
    public Result<Void> shipOrderItem(
            @PathVariable Long orderItemId,
            HttpServletRequest request  ) {
        try {
            Long sellerId = getUserIdFromToken(request);
            // 验证该订单项是否属于当前卖家
            boolean isValid = orderItemService.validateSellerOrderItem(orderItemId, sellerId);
            if (!isValid) {
                return Result.error("无权操作此订单");
            }
            
            // 更新订单项状态为已发货
            orderItemService.updateOrderItemStatus(orderItemId, OrderItemStatus.SHIPPED);
            
            // 发送短信或邮件通知顾客
            orderItemService.notifyCustomerAboutShipment(orderItemId);
            
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("发货失败：" + e.getMessage());
        }
    }
    // 卖家获取已发货订单项列表
    @GetMapping("/seller/shipped")
    @PreAuthorize("hasRole('SELLER')")
    public Result<Page<OrderItemDTO>> getSellerShippedOrderItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long sellerId = getUserIdFromToken(request);
            Page<OrderItemDTO> orderItems = orderItemService.getSellerShippedOrderItems(sellerId, page, size);
            return Result.success(orderItems);
        } catch (Exception e) {
            return Result.error("获取已发货订单项失败：" + e.getMessage());
        }
    }
    

    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败：" + e.getMessage());
        }
    }
}
