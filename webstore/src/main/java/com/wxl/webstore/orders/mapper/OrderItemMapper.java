package com.wxl.webstore.orders.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.webstore.orders.entity.OrderItem;
import com.wxl.webstore.orders.entity.Orders;

import io.lettuce.core.dynamic.annotation.Param;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-13
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    Page<OrderItem> selectSellerPendingOrderItems(Page<OrderItem> page, @Param("sellerId") Long sellerId);
    
    int countSellerPendingOrderItems(@Param("sellerId") Long sellerId);
    
    int isSellerOrderItem(@Param("orderItemId") Long orderItemId, @Param("sellerId") Long sellerId);

    Orders getOrderByOrderItemId(@Param("orderItemId") Long orderItemId);

    Page<OrderItem> selectSellerShippedOrderItems(Page<OrderItem> page, @Param("sellerId") Long sellerId);
}
