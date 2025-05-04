package com.wxl.webstore.log.purchaselog.service.impl;

import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.mapper.PurchaseLogMapper;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;
import com.wxl.webstore.common.enums.ProductCategory;
import com.wxl.webstore.common.utils.IpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品购买记录日志表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
@Service
public class PurchaseLogServiceImpl extends ServiceImpl<PurchaseLogMapper, PurchaseLog> implements PurchaseLogService {

    @Override
    public void recordPurchaseLog(Long userId, Long orderId, Long productId, 
                                 ProductCategory productCategory, Integer quantity,
                                 BigDecimal unitPrice, HttpServletRequest request) {
        PurchaseLog purchaseLog = new PurchaseLog();
        purchaseLog.setUserId(userId);
        purchaseLog.setOrderId(orderId);
        purchaseLog.setProductId(productId);
        purchaseLog.setProductCategory(productCategory);
        purchaseLog.setQuantity(quantity);
        purchaseLog.setUnitPrice(unitPrice);
        purchaseLog.setPurchaseTime(LocalDateTime.now());
        purchaseLog.setIpAddress(IpUtil.getIpAddr(request));
        purchaseLog.setCreateTime(LocalDateTime.now());
        
        save(purchaseLog);
    }
}
