package com.wxl.webstore.receiverInfo.service;

import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 收货地址信息表（级联软删除） 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
public interface ReceiverInfoService extends IService<ReceiverInfo> {
    //创建收货信息
    boolean createReceiverInfo(Long userId, String receiverName, String receiverPhone,
                                String provinceCode, String provinceName,
                               String cityCode, String cityName,
                               String districtCode, String districtName,
                               String detail, String label);

    //删除收货信息
    boolean deleteReceiverInfo(Long receiverId);

    //查找当前用户所有未删除的收货信息
    List<ReceiverInfo> getCustomerReceiverInfos(Long userId);

    //设置默认地址
    boolean setDefaultAddress(Long userId, Long receiverId);

    //部分更新地址信息
    boolean updateReceiverInfoPartially(Long receiverId, ReceiverInfo updateInfo, Long userId);

    //获取默认地址
    Long getDefaultReceiverInfo(Long userId);
}
