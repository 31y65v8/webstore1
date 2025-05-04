package com.wxl.webstore.browse.mapper;

import com.wxl.webstore.browse.entity.Browse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品浏览记录表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-22
 */
@Mapper
public interface BrowseMapper extends BaseMapper<Browse> {

    List<Browse> getSellerBrowseHistoryByProduct(Long userId, Long productId);

}
