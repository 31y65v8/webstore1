package com.wxl.webstore.orders.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.common.enums.OrderItemStatus;
import com.wxl.webstore.orders.dto.OrderItemDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
public interface OrderItemService extends IService<OrderItem> {
    void createOrderItem(List<OrderItem> orderItems);//创建订单项
    void deleteOrderItem(Long orderItemId);//真删除订单项
    void updateOrderItemQuantity(Long orderItemId, int quantity);//修改订单项数量
    List<OrderItem> getOrderItemsByOrderId(Long orderId);//根据订单id获取所有订单项
    OrderItem getOrderItemById(Long orderItemId);//根据订单项id获取订单项
    void updateOrderItemStatus(Long orderItemId, OrderItemStatus newStatus);//更新订单项状态
    Page<OrderItemDTO> getSellerPendingOrderItems(Long sellerId, int page, int size);//卖家获取待发货订单项列表
    int getSellerPendingOrderItemsCount(Long sellerId);//卖家获取待发货订单项数量
    boolean validateSellerOrderItem(Long orderItemId, Long sellerId);//验证卖家订单项
    void notifyCustomerAboutShipment(Long orderItemId);//通知顾客发货
    Page<OrderItemDTO> getSellerShippedOrderItems(Long sellerId, int page, int size);//卖家获取已发货订单项列表
}
