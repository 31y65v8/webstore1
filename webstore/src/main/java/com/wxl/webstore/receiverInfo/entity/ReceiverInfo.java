package com.wxl.webstore.receiverInfo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
    * 收货地址信息表（级联软删除）
    */
@Schema(description = "收货地址信息表（级联软删除）")
@Getter
@Setter
@TableName("receiver_info")
public class ReceiverInfo implements Serializable {
private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;
        @Schema(description = "关联 user.id")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;
        @Schema(description = "收货电话")
    @TableField("receiver_phone")
    private String receiverPhone;
        @Schema(description = "省份编码")
    @TableField("province_code")
    private String provinceCode;
        @Schema(description = "省份名称")
    @TableField("province_name")
    private String provinceName;
        @Schema(description = "城市编码")
    @TableField("city_code")
    private String cityCode;
        @Schema(description = "城市名称")
    @TableField("city_name")
    private String cityName;
        @Schema(description = "区县编码")
    @TableField("district_code")
    private String districtCode;
        @Schema(description = "区县名称")
    @TableField("district_name")
    private String districtName;
        @Schema(description = "详细地址信息")
    @TableField("detail")
    private String detail;
        @Schema(description = "地址标签（家/公司/学校）")
    @TableField("label")
    private String label;
        @Schema(description = "是否默认地址（0否1是）")
    @TableField("is_default")
    private Boolean isDefault;
        @Schema(description = "是否删除(0-未删除,1-已删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
        @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
        @Schema(description = "删除时间")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}