package com.wxl.webstore.cart.mapper;

import com.wxl.webstore.cart.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户购物车表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

}
