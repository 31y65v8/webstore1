package com.wxl.webstore.browse.service;

import com.wxl.webstore.browse.entity.Browse;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品浏览记录表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-22
 */
public interface BrowseService extends IService<Browse> {

    

    void recordBrowse(Long productId, Long userId, Integer duration, String clickTime);

    //为管理员提供每种商品的浏览记录
    List<Browse> getSellerBrowseHistoryByProduct(Long userId, Long productId);

    //为管理员提供指定时间内的的浏览记录
    //List<Browse> getSellerBrowseHistoryByTime(Long userId);

}
