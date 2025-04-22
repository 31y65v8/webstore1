package com.wxl.webstore.orders.listener;

import com.wxl.webstore.common.config.RabbitMQConfig;
import com.wxl.webstore.common.enums.OrderStatus;
import com.wxl.webstore.orders.entity.Orders;
import com.wxl.webstore.orders.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCancelListener {

    @Autowired
    private OrdersService ordersService;

    @RabbitListener(queues = "order.delay.queue")
    public void handleOrderCancel(String message) {
        log.info("收到订单取消消息: {}", message);
        try {
            Long orderId = Long.parseLong(message);
            log.info("解析后的订单ID: {}", orderId);
            // 查询订单
            Orders order = ordersService.getOrderById(orderId);
            log.info("查询订单结果: {}", order != null ? "订单存在" : "订单不存在");
            
            // 只有还处于PENDING状态的订单才需要取消
            if (order != null && order.getStatus() == OrderStatus.PENDING) {
                // 取消订单，添加取消原因
                ordersService.cancelOrder(orderId, "订单超时未支付，系统自动取消");
                log.info("订单{}已自动取消", orderId);
            } else {
                log.info("订单{}无需取消，当前状态: {}", orderId, 
                         order != null ? order.getStatus() : "订单不存在");
            }
        } catch (Exception e) {
            log.error("处理订单取消消息异常", e);
        }
    }
}
