package com.wxl.webstore.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.wxl.webstore.orders.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    
    @Autowired
    private IAcsClient acsClient;
    
    @Value("${aliyun.sms.sign-name}")
    private String signName;
    
    @Value("${aliyun.sms.template-code.shipment}")
    private String shipmentTemplateCode;
    
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        logger.error("此方法不直接支持发送原始消息，请使用模板方法");
        return false;
    }
    
    /**
     * 使用阿里云短信模板发送短信
     * 
     * @param phoneNumber 手机号码
     * @param templateCode 模板编码
     * @param templateParams 模板参数（JSON格式）
     * @return 是否发送成功
     */
    public boolean sendTemplateMessage(String phoneNumber, String templateCode, Map<String, String> templateParams) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        
        // 设置短信参数
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSON.toJSONString(templateParams));
        
        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            logger.info("短信API响应: {}", response.getData());
            
            // 解析JSON响应
            Map<String, Object> responseMap = JSON.parseObject(response.getData(), Map.class);
            String code = (String) responseMap.get("Code");
            
            // 根据阿里云文档，"OK"表示发送成功
            if ("OK".equals(code)) {
                logger.info("短信发送成功 - 手机号: {}", phoneNumber);
                return true;
            } else {
                String message = (String) responseMap.get("Message");
                logger.error("短信发送失败 - 手机号: {}, 错误码: {}, 错误信息: {}", 
                        phoneNumber, code, message);
                return false;
            }
        } catch (ServerException e) {
            logger.error("短信服务端异常 - 手机号: {}, 错误: {}", phoneNumber, e.getMessage());
            return false;
        } catch (ClientException e) {
            logger.error("短信客户端异常 - 手机号: {}, 错误: {}", phoneNumber, e.getErrMsg());
            return false;
        } catch (Exception e) {
            logger.error("短信发送未知异常 - 手机号: {}, 错误: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean sendShipmentNotification(String phoneNumber, String productName, String orderId, String receiverName) {
        // 构建短信模板参数
        // 注意：需要在阿里云控制台创建相应的短信模板，例如：
        // 模板内容: 尊敬的${name}，您购买的商品"${product}"已发货，订单号：${order}，请注意查收。
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("name", receiverName);
        templateParams.put("product", productName);
        templateParams.put("order", orderId);
        
        return sendTemplateMessage(phoneNumber, shipmentTemplateCode, templateParams);
    }
}
