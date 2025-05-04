package com.wxl.webstore.user_profile.dto;

import com.wxl.webstore.common.enums.ProductCategory;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Data
public class UserProfileAnalysisDTO {
    // 基础信息
    private Long userId;
    private String username;
    
    // 地域信息
    private RegionInfo regionInfo;

    // 消费能力分析
    private ConsumptionAnalysis consumptionAnalysis;
    
    // 行为分析
    private BehaviorAnalysis behaviorAnalysis;
    
    // 品类偏好分析
    private CategoryPreference categoryPreference;
    
    // 用户标签
    private List<String> tags;
    
    // 活跃度评分（0-100）
    private Long activityScore;
    
    // 最近更新时间
    private LocalDateTime lastUpdateTime;

    @Data
    public static class RegionInfo {
        private String provinceCode;
        private String provinceName;
        private String cityCode;
        private String cityName;
        // 用户所在地区的消费水平评级（高、中、低）
        private String regionalConsumptionLevel;
    }

    @Data
    public static class ConsumptionAnalysis {
        // 最近一次购买时间
        private LocalDateTime lastPurchaseTime;
        // 消费总额
        private BigDecimal totalPurchaseAmount;
        // 平均订单金额
        private BigDecimal averageOrderAmount;
        // 最近30天消费金额
        private BigDecimal last30DaysAmount;
        // 消费频率（次/月）
        private Long monthlyPurchaseFrequency;
        // 消费能力等级（HIGH, MEDIUM, LOW）
        private String consumptionLevel;
        // 消费时间分布（24小时制）
        private Map<Integer, Integer> purchaseTimeDistribution;
    }

    @Data
    public static class BehaviorAnalysis {
        // 登录行为
        private Long last30DaysLoginCount;
        private String preferredLoginTime; // 最常登录时段
        private String loginDevice; // 最常用登录设备
        
        // 浏览行为
        private Long last30DaysBrowseCount;
        private Double averageBrowseDuration; // 平均浏览时长（分钟）
        private Integer browseProductCount; // 浏览商品数
        
        // 购物车行为
        private Integer cartAddCount; // 加购次数
        private Double cartToOrderRate; // 加购转化率
        
        // 下单行为
        private Integer orderCount; // 下单次数
        private Double orderCompletionRate; // 订单完成率
    }

    @Data
    public static class CategoryPreference {
        // 主要关注的品类（按照浏览和购买频次综合排序）
        private List<CategoryAnalysis> topCategories;
        // 购买频次最高的品类
        private String mostPurchasedCategory;
        // 浏览时长最长的品类
        private String mostBrowsedCategory;
        // 品类偏好得分（0-100）
        private Map<String, Integer> categoryScores;
    }

    @Data
    public static class CategoryAnalysis {
        private String category;
        private Integer browseCount;
        private Integer purchaseCount;
        private BigDecimal totalAmount;
        private Double preferenceScore; // 偏好程度得分（0-100）
    }

    // 工具方法：计算用户活跃度评分
    public void calculateActivityScore() {
        // 基于登录频率、浏览行为、购买行为等计算活跃度评分
        Long loginScore = Math.min(this.behaviorAnalysis.getLast30DaysLoginCount() * 2, 30);
        Long browseScore = Math.min(this.behaviorAnalysis.getLast30DaysBrowseCount() / 10, 30);
        Long purchaseScore = Math.min(this.consumptionAnalysis.getMonthlyPurchaseFrequency() * 5, 40);
        
        this.activityScore = loginScore + browseScore + purchaseScore;
    }

    // 工具方法：生成用户标签
    public void generateTags() {
        List<String> tags = new ArrayList<>();
        
        // 1. 消费能力标签
        tags.add(this.consumptionAnalysis.getConsumptionLevel());
        
        // 2. 活跃度标签
        if (this.activityScore >= 80) {
            tags.add("超活跃用户");
        } else if (this.activityScore >= 60) {
            tags.add("活跃用户");
        } else if (this.activityScore >= 40) {
            tags.add("普通用户");
        } else {
            tags.add("低活跃用户");
        }
        
        // 3. 品类偏好标签
        this.categoryPreference.getTopCategories()
            .stream()
            .limit(2)
            .forEach(category -> tags.add("喜好_" + category.getCategory()));
        
        this.tags = tags;
    }
}
