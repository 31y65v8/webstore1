package com.wxl.webstore.orders.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.common.enums.OrderItemStatus;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;
import com.wxl.webstore.orders.dto.OrderItemDTO;
import com.wxl.webstore.orders.mapper.OrderItemMapper;
import com.wxl.webstore.orders.service.OrderItemService;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.orders.service.OrdersService;
import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import com.wxl.webstore.receiverInfo.service.ReceiverInfoService;
import com.wxl.webstore.orders.service.EmailService;
import com.wxl.webstore.orders.service.SmsService;
//import com.wxl.webstore.orders.service.NotificationLogService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    //@Autowired
    //private OrdersService ordersService;

    @Autowired
    private ReceiverInfoService receiverInfoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    //@Autowired
    //private NotificationLogService notificationLogService;

    @Override
    @Transactional
    public void createOrderItem(List<OrderItem> orderItems) {
        this.saveBatch(orderItems);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        this.removeById(orderItemId);
    }

    @Override
    @Transactional
    public void updateOrderItemQuantity(Long orderItemId, int newQuantity) {
        // 验证参数
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        
        // 获取当前订单项
        OrderItem orderItem = this.getById(orderItemId);
        if (orderItem == null) {
            throw new RuntimeException("订单项不存在");
        }
        
        // 获取数量变化
        int quantityDifference = newQuantity - orderItem.getQuantity();
        
        // 如果数量没有变化，直接返回
        if (quantityDifference == 0) {
            return;
        }
        
        // 更新商品库存
        Long productId = orderItem.getProductId();
        if (quantityDifference > 0) {
            // 如果新数量大于旧数量，减少库存
            productService.decreaseStock(productId, quantityDifference);
        } else {
            // 如果新数量小于旧数量，增加库存
            productService.increaseStock(productId, -quantityDifference);
        }
        
        // 更新订单项数量和总价
        BigDecimal unitPrice = orderItem.getProductPrice();
        BigDecimal newTotalPrice = unitPrice.multiply(new BigDecimal(newQuantity));
        
        orderItem.setQuantity(newQuantity);
        orderItem.setTotalPrice(newTotalPrice);
        this.updateById(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return this.list(queryWrapper);
    }

    @Override
    public OrderItem getOrderItemById(Long orderItemId) {
        return this.getById(orderItemId);
    }

    @Override
    @Transactional
    public void updateOrderItemStatus(Long orderItemId, OrderItemStatus newStatus) {
        OrderItem orderItem = this.getById(orderItemId);
        orderItem.setStatus(newStatus);
        this.updateById(orderItem);
        // 在订单状态为已发货时，通知用户
        if (newStatus == OrderItemStatus.SHIPPED) {
            notifyUser(orderItem);
        }
    }
     // 通知用户订单已发货
     private void notifyUser(OrderItem orderItem) {
        // 根据用户注册方式选择通知方式（短信或邮件）
        // 此处需要调用用户服务获取用户信息
        // 实现短信或邮件发送逻辑
    }

    @Override
    public Page<OrderItemDTO> getSellerPendingOrderItems(Long sellerId, int page, int size) {
        Page<OrderItem> pageParam = new Page<>(page, size);
        
        // 查询当前卖家负责的所有已支付但未发货的订单项
        Page<OrderItem> orderItemPage = baseMapper.selectSellerPendingOrderItems(pageParam, sellerId);
        
        // 转换为DTO
        List<OrderItemDTO> records = orderItemPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Page<OrderItemDTO> resultPage = new Page<>();
        resultPage.setRecords(records);
        resultPage.setCurrent(orderItemPage.getCurrent());
        resultPage.setSize(orderItemPage.getSize());
        resultPage.setTotal(orderItemPage.getTotal());
        resultPage.setPages(orderItemPage.getPages());
        
        return resultPage;
    }

    @Override
    public int getSellerPendingOrderItemsCount(Long sellerId) {
        return baseMapper.countSellerPendingOrderItems(sellerId);
    }

    @Override
    public boolean validateSellerOrderItem(Long orderItemId, Long sellerId) {
        // 验证该订单项是否属于当前卖家的商品
        return baseMapper.isSellerOrderItem(orderItemId, sellerId) > 0;
    }

    @Override
    public void notifyCustomerAboutShipment(Long orderItemId) {
        // 获取订单项信息，包括商品名称和收件人信息
        OrderItemDTO orderItem = convertToDTO(getOrderItemById(orderItemId));
        
        // 获取客户的联系方式（邮箱或手机号）
        String contactInfo = userService.getUserContactInfo(orderItem.getUserId());
        
        // 根据联系方式类型选择发送方式
        if (contactInfo.contains("@")) {
            // 发送邮件
            String subject = "您的订单商品已发货";
            String content = String.format(
                    "尊敬的客户，您购买的商品 %s 已发货，请注意查收。订单号：%s",
                    orderItem.getProductName(),
                    orderItem.getOrderId()
            );
            emailService.sendEmail(contactInfo, subject, content);
        } else {
            // 发送短信
            String message = String.format(
                    "【网上商城】您购买的商品 %s 已发货，请注意查收。订单号：%s",
                    orderItem.getProductName(),
                    orderItem.getOrderId()
            );
            smsService.sendSms(contactInfo, message);
        }
        
        // 记录通知日志
       // notificationLogService.recordShipmentNotification(orderItemId, contactInfo);
    }

    // 辅助方法：将OrderItem转换为DTO，包含更多的信息
    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(orderItem, dto);
        
        // 获取商品信息
        Product product = productService.getById(orderItem.getProductId());
        if (product != null) {
            dto.setProductName(product.getName());
            dto.setProductImage(product.getImgurl());
            dto.setProductPrice(product.getPrice());
        }
        
        // 使用新方法直接获取订单信息，不再依赖OrdersService
        Orders order = baseMapper.getOrderByOrderItemId(orderItem.getId());
        if (order != null) {
            // 获取收货地址信息
            ReceiverInfo receiverInfo = receiverInfoService.getById(order.getReceiverId());
            if (receiverInfo != null) {
                dto.setReceiverName(receiverInfo.getReceiverName());
                dto.setReceiverPhone(receiverInfo.getReceiverPhone());
                dto.setProvinceName(receiverInfo.getProvinceName());
                dto.setCityName(receiverInfo.getCityName());
                dto.setDistrictName(receiverInfo.getDistrictName());
                dto.setAddressDetail(receiverInfo.getDetail());
            }
            
            dto.setUserId(order.getUserId());
            dto.setCreateTime(order.getCreateTime());
            dto.setTotalPrice(order.getTotalPrice());
        }
        
        return dto;
    }
    // 卖家获取已发货订单项列表
    @Override
    public Page<OrderItemDTO> getSellerShippedOrderItems(Long sellerId, int page, int size) {
        Page<OrderItem> pageParam = new Page<>(page, size);
        
        // 查询当前卖家负责的所有已发货的订单项
        Page<OrderItem> orderItemPage = baseMapper.selectSellerShippedOrderItems(pageParam, sellerId);
        
        // 转换为DTO
        List<OrderItemDTO> records = orderItemPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Page<OrderItemDTO> resultPage = new Page<>();
        resultPage.setRecords(records);
        resultPage.setCurrent(orderItemPage.getCurrent());
        resultPage.setSize(orderItemPage.getSize());
        resultPage.setTotal(orderItemPage.getTotal());
        resultPage.setPages(orderItemPage.getPages());
        
        return resultPage;
    }

}
