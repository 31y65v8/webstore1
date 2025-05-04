package com.wxl.webstore.orders.service;

public interface EmailService {
    /**
     * 发送邮件
     * @param to 收件人邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendEmail(String to, String subject, String content);
    
    /**
     * 发送商品已发货通知邮件
     * @param to 收件人邮箱地址
     * @param productName 商品名称
     * @param orderId 订单号
     * @param receiverName 收件人姓名
     * @return 是否发送成功
     */
    boolean sendShipmentNotification(String to, String productName, String orderId, String receiverName);
}
