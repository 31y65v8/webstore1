package com.wxl.webstore.browse.service.impl;

import com.wxl.webstore.browse.entity.Browse;
import com.wxl.webstore.browse.mapper.BrowseMapper;
import com.wxl.webstore.browse.service.BrowseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品浏览记录表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-22
 */
@Service
public class BrowseServiceImpl extends ServiceImpl<BrowseMapper, Browse> implements BrowseService {

    @Override
    public void recordBrowse(Long productId, Long userId, Integer duration, String clickTime) {
        Browse browse = new Browse();
        browse.setProductId(productId);
        browse.setUserId(userId);
        browse.setTime(LocalDateTime.parse(clickTime));
        browse.setCreateTime(LocalDateTime.now());
        
        // 设置停留时间
        browse.setDuration(duration);
        save(browse);
    }

    @Override
    public List<Browse> getSellerBrowseHistoryByProduct(Long userId, Long productId) {
        return baseMapper.getSellerBrowseHistoryByProduct(userId, productId);
    }
}
