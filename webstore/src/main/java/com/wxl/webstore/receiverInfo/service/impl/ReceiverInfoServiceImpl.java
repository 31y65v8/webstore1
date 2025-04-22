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

    /**
     * 设置默认地址
     * 将指定地址设置为默认，同时取消该用户其他地址的默认标记
     * 
     * @param userId 用户ID
     * @param receiverId 要设置为默认的地址ID
     * @return 操作是否成功
     */
    public boolean setDefaultAddress(Long userId, Long receiverId) {
        // 1. 检查地址是否存在且属于该用户
        ReceiverInfo receiverInfo = this.getOne(
            new LambdaQueryWrapper<ReceiverInfo>()
                .eq(ReceiverInfo::getId, receiverId)
                .eq(ReceiverInfo::getUserId, userId)
                .eq(ReceiverInfo::getIsDeleted, false)
        );
        
        if (receiverInfo == null) {
            return false; // 地址不存在或不属于该用户
        }
        
        // 2. 取消该用户所有地址的默认标记
        List<ReceiverInfo> userAddresses = this.list(
            new LambdaQueryWrapper<ReceiverInfo>()
                .eq(ReceiverInfo::getUserId, userId)
                .eq(ReceiverInfo::getIsDeleted, false)
                .eq(ReceiverInfo::getIsDefault, true)
        );
        
        for (ReceiverInfo address : userAddresses) {
            address.setIsDefault(false);
            address.setUpdateTime(LocalDateTime.now());
            this.updateById(address);
        }
        
        // 3. 将指定地址设为默认
        receiverInfo.setIsDefault(true);
        receiverInfo.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(receiverInfo);
    }

    /**
     * 部分更新地址信息
     * 只更新非空字段
     * 
     * @param receiverId 地址ID
     * @param updateInfo 包含要更新字段的对象
     * @param userId 用户ID（用于验证地址所有权）
     * @return 操作是否成功
     */
    public boolean updateReceiverInfoPartially(Long receiverId, ReceiverInfo updateInfo, Long userId) {
        // 1. 检查地址是否存在且属于该用户
        ReceiverInfo existingInfo = this.getOne(
            new LambdaQueryWrapper<ReceiverInfo>()
                .eq(ReceiverInfo::getId, receiverId)
                .eq(ReceiverInfo::getUserId, userId)
                .eq(ReceiverInfo::getIsDeleted, false)
        );
        
        if (existingInfo == null) {
            return false; // 地址不存在或不属于该用户
        }
        
        // 2. 只更新非空字段
        boolean needUpdate = false;
        
        if (updateInfo.getReceiverName() != null) {
            existingInfo.setReceiverName(updateInfo.getReceiverName());
            needUpdate = true;
        }
        
        if (updateInfo.getReceiverPhone() != null) {
            existingInfo.setReceiverPhone(updateInfo.getReceiverPhone());
            needUpdate = true;
        }
        
        if (updateInfo.getProvinceCode() != null) {
            existingInfo.setProvinceCode(updateInfo.getProvinceCode());
            needUpdate = true;
        }
        
        if (updateInfo.getProvinceName() != null) {
            existingInfo.setProvinceName(updateInfo.getProvinceName());
            needUpdate = true;
        }
        
        if (updateInfo.getCityCode() != null) {
            existingInfo.setCityCode(updateInfo.getCityCode());
            needUpdate = true;
        }
        
        if (updateInfo.getCityName() != null) {
            existingInfo.setCityName(updateInfo.getCityName());
            needUpdate = true;
        }
        
        if (updateInfo.getDistrictCode() != null) {
            existingInfo.setDistrictCode(updateInfo.getDistrictCode());
            needUpdate = true;
        }
        
        if (updateInfo.getDistrictName() != null) {
            existingInfo.setDistrictName(updateInfo.getDistrictName());
            needUpdate = true;
        }
        
        if (updateInfo.getDetail() != null) {
            existingInfo.setDetail(updateInfo.getDetail());
            needUpdate = true;
        }
        
        if (updateInfo.getLabel() != null) {
            existingInfo.setLabel(updateInfo.getLabel());
            needUpdate = true;
        }
        
        // 如果没有字段需要更新，则直接返回true
        if (!needUpdate) {
            return true;
        }
        
        // 3. 更新时间戳
        existingInfo.setUpdateTime(LocalDateTime.now());
        
        // 4. 执行更新
        return this.updateById(existingInfo);
    }

    //获取默认地址
    public Long getDefaultReceiverInfo(Long userId) {
        ReceiverInfo defaultReceiverInfo = this.getOne(new LambdaQueryWrapper<ReceiverInfo>()
                .eq(ReceiverInfo::getUserId, userId)
                .eq(ReceiverInfo::getIsDefault, true)
                .eq(ReceiverInfo::getIsDeleted, false));
        if (defaultReceiverInfo == null) {
            throw new RuntimeException("请先设置默认收货地址");
        }
        return defaultReceiverInfo.getId();
    }

}
