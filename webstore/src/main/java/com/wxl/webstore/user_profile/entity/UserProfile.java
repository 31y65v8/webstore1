package com.wxl.webstore.user_profile.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "")
@Getter
@Setter
@TableName("user_profile")
public class UserProfile implements Serializable {
private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;
    @TableField("user_id")
    private Long userId;
    //地域信息
    @TableField("province_code")
    private String province;
    @TableField("city_code")
    private String city;
    //RFM指标
    @TableField("last_purchase_time")
    private LocalDateTime lastPurchaseTime;//最近一次购买时间
    @TableField("total_purchase_amount")
    private BigDecimal totalPurchaseAmount;//总消费金额
    @TableField("average_order_amount")
    private BigDecimal averageOrderAmount;//平均订单金额
    @TableField("last30days_purchase_frequency")
    private Long last30daysPurchaseFrequency;//最近30天购买频率
    @TableField("recency_score")
    private Integer recencyScore;
    @TableField("frequency_score")
    private Integer frequencyScore;
    @TableField("monetary_score")
    private Integer monetaryScore;
    @TableField("rmf_level")
    private String rmfLevel;

    //购物偏好
    @TableField("primary_category")
    private String primaryCategory;
    @TableField("secondary_category")
    private String secondaryCategory;
    //行为特征
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    @TableField("last30days_login_frequency")
    private Long last30daysLoginFrequency;
    @TableField("last30days_browse_frequency")
    private Long last30daysBrowseFrequency;
    //用户标签
    @TableField("tags")
    private String tags;//逗号分隔的标签字符串
    //时间戳
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}