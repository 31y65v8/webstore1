package com.wxl.webstore.orders.controller;

import com.wxl.webstore.common.enums.OrderItemStatus;
import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;
import com.wxl.webstore.orders.service.OrdersService;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PurchaseLogService purchaseLogService;

    @Autowired
    private ProductService productService;

    /*
     * 从请求头获取token并解析userId
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            System.out.println("从token中解析到的userId: " + userId);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败：" + e.getMessage());
        }
    }
    
    
    /**
     * 从购物车创建订单
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<Void> createOrderFromCart(HttpServletRequest request, 
                                            @RequestParam(required = false) BigDecimal totalPrice) {
        try {
            Long userId = getUserIdFromToken(request);
            log.info("开始创建订单，用户ID: {}, 前端计算的总价: {}", userId, totalPrice);
            
            // 调用服务方法，并传递总价参数
            ordersService.createOrderFromCart(userId, totalPrice);
            
            log.info("订单创建成功，用户ID: {}", userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
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
    @PreAuthorize("hasAnyRole('SELLER')")
    @OperationLogAnnoce(module = "订单模块", operation = "更新订单状态")
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
    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Result<List<Orders>> getOrdersByUserId(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
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

    /**
     * 支付订单（模拟支付）
     */
    @PostMapping("/{orderId}/pay")
    public Result<String> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        // 从token获取用户ID
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error("未授权的请求");
        }
        
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String account = jwtUtil.getAccountFromToken(token);
            
            // 获取订单信息，验证订单归属和状态
            Orders order = ordersService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            if (!order.getUserId().equals(userId)) {
                return Result.error( "无权操作此订单");
            }
            
            if (order.getStatus() != OrderStatus.PENDING) {
                return Result.error("订单状态不正确，无法支付");
            }
            
            // 更新订单状态为已支付
            ordersService.updateOrderStatus(orderId, OrderStatus.PAID);
            
            // 获取订单项
            List<OrderItem> orderItems = ordersService.getOrderItemsByOrderId(orderId);
            
            // 记录购买日志
            for (OrderItem item : orderItems) {
                Product product = productService.getById(item.getProductId());
                if (product != null) {
                    purchaseLogService.recordPurchaseLog(
                        userId,
                        orderId,
                        item.getProductId(),
                        product.getCategory(),
                        item.getQuantity(),
                        item.getProductPrice(),
                        request
                    );
                }
            }
            
            return Result.success("订单支付成功");
        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }
}
