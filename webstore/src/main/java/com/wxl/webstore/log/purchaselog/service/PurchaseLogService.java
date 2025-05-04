package com.wxl.webstore.log.purchaselog.service;

import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.common.enums.ProductCategory;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * <p>
 * 商品购买记录日志表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
public interface PurchaseLogService extends IService<PurchaseLog> {

    /**
     * 记录商品购买日志
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param productId 商品ID
     * @param productCategory 商品类别
     * @param quantity 购买数量
     * @param unitPrice 商品单价
     * @param request HTTP请求对象
     */
    void recordPurchaseLog(Long userId, Long orderId, Long productId, 
                          ProductCategory productCategory, Integer quantity,
                          BigDecimal unitPrice, HttpServletRequest request);
}
