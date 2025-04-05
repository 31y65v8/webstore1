package com.wxl.webstore.receiverInfo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import com.wxl.webstore.receiverInfo.mapper.ReceiverInfoMapper;
import com.wxl.webstore.receiverInfo.service.ReceiverInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 收货地址信息表（级联软删除） 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@Service
public class ReceiverInfoServiceImpl extends ServiceImpl<ReceiverInfoMapper, ReceiverInfo> implements ReceiverInfoService {

    @Autowired
    private ReceiverInfoMapper receiverInfoMapper;

    // 新建收货信息
    public boolean createReceiverInfo(Long userId, String receiverName, String receiverPhone,
                                      String provinceCode, String provinceName, String cityCode,
                                      String cityName, String districtCode, String districtName,
                                      String detail, String label) {
        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo.setUserId(userId);
        receiverInfo.setReceiverName(receiverName);
        receiverInfo.setReceiverPhone(receiverPhone);
        receiverInfo.setProvinceCode(provinceCode);
        receiverInfo.setProvinceName(provinceName);
        receiverInfo.setCityCode(cityCode);
        receiverInfo.setCityName(cityName);
        receiverInfo.setDistrictCode(districtCode);
        receiverInfo.setDistrictName(districtName);
        receiverInfo.setDetail(detail);
        receiverInfo.setLabel(label);
        receiverInfo.setIsDefault(false); // 默认不是默认地址
        receiverInfo.setIsDeleted(false); // 未删除
        receiverInfo.setCreateTime(LocalDateTime.now());
        receiverInfo.setUpdateTime(LocalDateTime.now());

        return this.save(receiverInfo); // 使用 MyBatis-Plus 提供的 save 方法插入数据
    }


    //删除收货信息
    public boolean deleteReceiverInfo(Long receiverId) {
        ReceiverInfo receiverInfo = this.getById(receiverId);
        if(receiverInfo == null) {
            return false;
        }
        receiverInfo.setIsDeleted(true);
        receiverInfo.setUpdateTime(LocalDateTime.now()); // 更新时间
        receiverInfo.setDeletedAt(LocalDateTime.now()); // 设置删除时间
        // 执行更新操作
        return this.updateById(receiverInfo); // 使用 MyBatis-Plus 提供的 updateById 方法更新记录
    }

    //获取当前用户所有未删除的收货信息
    public List<ReceiverInfo> getCustomerReceiverInfos(Long userId) {
        return this.list(new LambdaQueryWrapper<ReceiverInfo>()
                .eq(ReceiverInfo::getUserId, userId)  // 过滤当前用户的地址
                .eq(ReceiverInfo::getIsDeleted, 0)   // 只查询未删除的地址
                .orderByDesc(ReceiverInfo::getUpdateTime)); // 按更新时间降序
    }

}
