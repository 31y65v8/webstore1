package com.wxl.webstore.orders.service.impl;

import com.wxl.webstore.orders.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${spring.mail.nickname:网上商城}")
    private String nickname;
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置发件人、收件人、主题和内容
            helper.setFrom(String.format("%s <%s>", nickname, fromEmail));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true表示内容是HTML格式
            helper.setSentDate(new Date());
            
            // 发送邮件
            mailSender.send(message);
            logger.info("邮件发送成功 - 收件人: {}, 主题: {}", to, subject);
            return true;
        } catch (MessagingException e) {
            logger.error("邮件发送失败 - 收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendShipmentNotification(String to, String productName, String orderId, String receiverName) {
        String subject = "您购买的商品已发货 - 订单号: " + orderId;
        
        // 构建HTML格式的邮件内容
        String content = String.format(
            "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: #fff; padding: 30px; border-radius: 5px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
            "<h2 style='color: #333; text-align: center; margin-bottom: 20px;'>商品已发货通知</h2>" +
            "<p>尊敬的 <strong>%s</strong> 用户，您好！</p>" +
            "<p>您购买的商品 <strong style='color: #1890ff;'>%s</strong> 已发货。</p>" +
            "<div style='background-color: #f5f5f5; padding: 15px; margin: 15px 0; border-radius: 5px;'>" +
            "<p style='margin: 0;'><strong>订单编号:</strong> %s</p>" +
            "</div>" +
            "<p>您可以登录您的账户查看物流信息。如有任何问题，请联系我们的客服。</p>" +
            "<p style='text-align: center; margin-top: 30px;'><a href='http://localhost:8080' style='background-color: #1890ff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;'>登录查看详情</a></p>" +
            "<p style='color: #999; font-size: 12px; text-align: center; margin-top: 30px;'>此邮件由系统自动发出，请勿直接回复</p>" +
            "</div></div>",
            receiverName, productName, orderId
        );
        
        return sendEmail(to, subject, content);
    }
}
