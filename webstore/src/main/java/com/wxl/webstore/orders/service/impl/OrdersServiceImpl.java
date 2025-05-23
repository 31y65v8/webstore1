package com.wxl.webstore.orders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.cart.entity.Cart;
import com.wxl.webstore.cart.service.CartService;
import com.wxl.webstore.common.enums.OrderItemStatus;
import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.common.service.RabbitMQSender;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;
import com.wxl.webstore.orders.mapper.OrdersMapper;
import com.wxl.webstore.orders.service.OrderItemService;
import com.wxl.webstore.orders.service.OrdersService;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import com.wxl.webstore.receiverInfo.service.ReceiverInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private RabbitMQSender rabbitMQSender;
    
    @Autowired
    private ReceiverInfoService receiverInfoService;

   

    private String getProductName(Long productId) {
        // 实现获取商品名称的逻辑
        return productService.getProductNameById(productId);
    }
    
    private BigDecimal getProductPrice(Long productId) {
        // 实现获取商品价格的逻辑
        return productService.getProductPriceById(productId);
    }

    
        
    @Override
    @Transactional
    public void createOrderFromCart(Long userId, BigDecimal frontEndTotalPrice) {
        // 获取选中的购物车项
        List<Cart> selectedCarts = cartService.getSelectedItems(userId);
            
        if (selectedCarts.isEmpty()) {
            throw new RuntimeException("没有选中的商品");
        }

        // 检查所有商品库存是否充足
        for (Cart cart : selectedCarts) {
            Product product = productService.getProductById(cart.getProductId());
            if (product.getStorage() < cart.getQuantity()) {
                throw new RuntimeException("商品【" + product.getName() + "】库存不足，当前库存：" + product.getStorage());
           }
        }
       // 计算后端总价
    //BigDecimal backendTotalPrice = BigDecimal.ZERO;
    //for (Cart cart : selectedCarts) {
    //    BigDecimal price = getProductPrice(cart.getProductId());
    //    BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cart.getQuantity()));
    //    backendTotalPrice = backendTotalPrice.add(itemTotal);
    //}
    
    
    
        // 获取默认收货地址
        Long defaultReceiverInfo = receiverInfoService.getDefaultReceiverInfo(userId);
        // 创建订单
        Orders order = new Orders();
        order.setReceiverId(defaultReceiverInfo);
        order.setUserId(userId);
        order.setTotalPrice(frontEndTotalPrice);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        this.save(order);
        
        // 转换为订单项并减少库存
    List<OrderItem> orderItems = new ArrayList<>();
    
    for (Cart cart : selectedCarts) {
        try {
            // 减少库存（包含分布式锁逻辑）
            productService.decreaseStock(cart.getProductId(), cart.getQuantity());
            // 增加销量
            productService.increaseSales(cart.getProductId(), cart.getQuantity());
            

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductName(getProductName(cart.getProductId()));
            orderItem.setProductPrice(getProductPrice(cart.getProductId()));
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(orderItem.getProductPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItem.setStatus(OrderItemStatus.PENDING);
            orderItems.add(orderItem);
        } catch (Exception e) {
            // 如果某个商品处理失败，回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("创建订单失败：" + e.getMessage(), e);
         }
    }
        
        // 保存订单项
        orderItemService.createOrderItem(orderItems);
        
        // 清除购物车中已下单的商品
        cartService.clearSelectedItems(userId);

        // 更新商品库存
        for (OrderItem item : orderItems) {
            productService.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // 发送延迟消息，15分钟后取消未支付订单
        log.info("准备发送订单取消延迟消息，订单ID: {}", order.getId());
        rabbitMQSender.sendOrderCancelMessage(order.getId(), 15 * 60 * 1000);
        log.info("订单取消延迟消息已发送");
    }
        
    @Override
    @Transactional
    public void cancelOrder(Long orderId, String cancelReason) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有在未付款状态才能取消订单
        if (order.getStatus() == OrderStatus.PENDING) {
            // 先获取订单项信息
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
            
            try {
                // 更新订单状态
                order.setUpdateTime(LocalDateTime.now());
                order.setStatus(OrderStatus.CANCELLED);
                order.setCancelReason(cancelReason);
                this.updateById(order);
                
                // 恢复商品库存
                for (OrderItem item : orderItems) {
                    try {
                        // 每个商品单独处理，独立事务，确保单个商品失败不影响其他商品
                        productService.increaseStock(item.getProductId(), item.getQuantity());
                    } catch (Exception e) {
                        // 记录异常，但继续处理其他商品
                        log.error("恢复商品库存失败，商品ID: {}, 数量: {}, 错误: {}", 
                                 item.getProductId(), item.getQuantity(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                // 如果更新订单状态失败，则回滚整个事务
                throw new RuntimeException("取消订单失败: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("订单已付款，无法取消");
        }
    }

    // 为了保持兼容性，重载原方法
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        cancelOrder(orderId, "订单超时未支付，系统自动取消");
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(newStatus);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        
        if(newStatus == OrderStatus.PAID){
            //获取订单项
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
            //更新订单项状态
            for(OrderItem orderItem : orderItems){
                orderItem.setStatus(OrderItemStatus.PAID);
                orderItemService.updateOrderItemStatus(orderItem.getId(), OrderItemStatus.PAID);
            }
        }


        
    }

    @Override
    public List<Orders> getOrdersByUserId(Long userId) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    @Override
    public Orders getOrderById(Long orderId) {
        return this.getById(orderId);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    @Override
    public List<Orders> findExpiredPendingOrders(int minutes) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        // 查找PENDING状态的订单
        queryWrapper.eq("status", OrderStatus.PENDING);
        // 查找创建时间早于指定分钟数的订单
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(minutes);
        queryWrapper.lt("create_time", expirationTime);
        return this.list(queryWrapper);
    }
    
    @Override
    public Long getReceiverId(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order.getReceiverId();
    }

}
