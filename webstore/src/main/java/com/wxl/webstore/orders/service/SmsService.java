package com.wxl.webstore.orders.service;

import java.util.Map;

public interface SmsService {
    /**
     * 发送短信
     * @param phoneNumber 手机号码
     * @param message 短信内容
     * @return 是否发送成功
     */
    boolean sendSms(String phoneNumber, String message);
    
    /**
     * 使用模板发送短信
     * @param phoneNumber 手机号码
     * @param templateCode 模板代码
     * @param templateParams 模板参数
     * @return 是否发送成功
     */
    boolean sendTemplateMessage(String phoneNumber, String templateCode, Map<String, String> templateParams);
    
    /**
     * 发送商品已发货通知短信
     * @param phoneNumber 手机号码
     * @param productName 商品名称
     * @param orderId 订单号
     * @param receiverName 收件人姓名
     * @return 是否发送成功
     */
    boolean sendShipmentNotification(String phoneNumber, String productName, String orderId, String receiverName);
}
