package com.wxl.webstore.receiverInfo.mapper;

import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 收货地址信息表（级联软删除） Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@Mapper
public interface ReceiverInfoMapper extends BaseMapper<ReceiverInfo> {

}
