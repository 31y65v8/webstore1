package com.wxl.webstore.receiverInfo.dto;

import java.time.LocalDateTime;
import com.wxl.webstore.receiverInfo.entity.ReceiverInfo;
import lombok.Data;

@Data
public class ReceiverInfoDTO {
    private String id;
    private String userId;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String provinceCode;
    private String cityName;
    private String cityCode;
    private String districtName;
    private String districtCode;
    private String detail;  
    private String label;
    private Boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deletedAt;
    
    // 从实体转换为DTO的静态方法
    public static ReceiverInfoDTO fromEntity(ReceiverInfo entity) {
        if (entity == null) return null;
        
        ReceiverInfoDTO dto = new ReceiverInfoDTO();
        dto.setId(String.valueOf(entity.getId()));
        dto.setUserId(String.valueOf(entity.getUserId()));
        dto.setReceiverName(entity.getReceiverName());
        dto.setReceiverPhone(entity.getReceiverPhone());
        dto.setProvinceName(entity.getProvinceName());
        dto.setCityName(entity.getCityName());
        dto.setDistrictName(entity.getDistrictName());
        dto.setDetail(entity.getDetail());
        dto.setLabel(entity.getLabel());
        dto.setIsDefault(entity.getIsDefault());
        
        return dto;
    }
}
