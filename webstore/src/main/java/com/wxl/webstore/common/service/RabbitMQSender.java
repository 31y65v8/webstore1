package com.wxl.webstore.common.service;

import com.wxl.webstore.common.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RabbitMQSender {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送延迟消息
     * @param orderId 订单ID
     * @param delayTime 延迟时间(毫秒)
     */
    public void sendOrderCancelMessage(Long orderId, long delayTime) {
        log.info("发送订单取消消息，原始订单ID: {}", orderId);
        // 转换/序列化过程
        String message = String.valueOf(orderId); // 或其他序列化方式
        log.info("序列化后的订单消息: {}", message);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_DELAY_EXCHANGE, 
            RabbitMQConfig.ORDER_DELAY_ROUTING_KEY, 
            message,
            messagePostProcessor -> {
                // 对于rabbitmq_delayed_message_exchange插件，使用x-delay头
                messagePostProcessor.getMessageProperties().setHeader("x-delay", (int) delayTime);
                return messagePostProcessor;
            }
        );
    }
}
