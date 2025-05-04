package com.wxl.webstore.user_profile.service.impl;

import com.wxl.webstore.user_profile.dto.UserProfileAnalysisDTO;
import com.wxl.webstore.user_profile.entity.UserProfile;
import com.wxl.webstore.user_profile.mapper.UserProfileMapper;
import com.wxl.webstore.user_profile.service.UserProfileAnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;
import com.wxl.webstore.log.loginlog.entity.LoginLog;
import com.wxl.webstore.log.loginlog.service.LoginLogService;
import com.wxl.webstore.browse.entity.Browse;
import com.wxl.webstore.browse.service.BrowseService;
import com.wxl.webstore.common.enums.ProductCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户画像分析服务实现类
 *
 * @author wxl
 * @since 2025-04-29
 */
@Slf4j
@Service
public class UserProfileAnalysisServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> 
    implements UserProfileAnalysisService {

    @Autowired
    private PurchaseLogService purchaseLogService;
    
    @Autowired
    private LoginLogService loginLogService;
    
    @Autowired
    private BrowseService browseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeAndUpdateUserProfile(Long userId) {
        UserProfile profile = getBaseMapper().selectByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setCreateTime(LocalDateTime.now());
        }
        
        try {
            // 1. 分析购买行为
            analyzePurchaseBehavior(profile);
            
            // 2. 分析登录行为
            analyzeLoginBehavior(profile);
            
            // 3. 分析浏览行为
            analyzeBrowseBehavior(profile);
            
            // 4. 生成用户标签
            generateUserTags(profile);
            
            profile.setUpdateTime(LocalDateTime.now());
            saveOrUpdate(profile);
        } catch (Exception e) {
            log.error("分析用户画像失败，userId: {}", userId, e);
            throw e;
        }
    }
    
    private void analyzePurchaseBehavior(UserProfile profile) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<PurchaseLog> purchaseLogs = purchaseLogService.list(new LambdaQueryWrapper<PurchaseLog>()
            .eq(PurchaseLog::getUserId, profile.getUserId())
            .ge(PurchaseLog::getPurchaseTime, thirtyDaysAgo));
            
        if (purchaseLogs.isEmpty()) {
            profile.setTotalPurchaseAmount(BigDecimal.ZERO);
            profile.setLast30daysPurchaseFrequency((long) 0);
            return;
        }
        
        // 计算总购买金额
        BigDecimal totalAmount = purchaseLogs.stream()
            .map(log -> log.getUnitPrice().multiply(new BigDecimal(log.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // 计算平均订单金额
        BigDecimal avgOrderAmount = totalAmount.divide(
            new BigDecimal(purchaseLogs.size()), 2, RoundingMode.HALF_UP);
            
        // 统计品类偏好
        Map<ProductCategory, Long> categoryCount = purchaseLogs.stream()
            .collect(Collectors.groupingBy(
                PurchaseLog::getProductCategory,
                Collectors.counting()
            ));
            
        // 获取最常购买的品类
        Optional<Map.Entry<ProductCategory, Long>> primaryCategory = categoryCount.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
            
        profile.setTotalPurchaseAmount(totalAmount);
        profile.setAverageOrderAmount(avgOrderAmount);
        profile.setLast30daysPurchaseFrequency((long) purchaseLogs.size());
        profile.setLastPurchaseTime(
            purchaseLogs.stream()
                .map(PurchaseLog::getPurchaseTime)
                .max(LocalDateTime::compareTo)
                .orElse(null)
        );
        primaryCategory.ifPresent(entry -> profile.setPrimaryCategory(entry.getKey().name()));
    }
    
    private void analyzeLoginBehavior(UserProfile profile) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 获取登录次数
        Long loginCount = loginLogService.count(new LambdaQueryWrapper<LoginLog>()
            .eq(LoginLog::getUserId, profile.getUserId())
            .ge(LoginLog::getLoginTime, thirtyDaysAgo));
            
        // 获取最后登录时间
        LocalDateTime lastLoginTime = loginLogService.getLastLoginTime(profile.getUserId());
        
        profile.setLast30daysLoginFrequency(loginCount);
        profile.setLastLoginTime(lastLoginTime);
    }
    
    private void analyzeBrowseBehavior(UserProfile profile) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 获取浏览次数
        Long browseCount = browseService.count(new LambdaQueryWrapper<Browse>()
            .eq(Browse::getUserId, profile.getUserId())
            .ge(Browse::getCreateTime, thirtyDaysAgo));
            
        profile.setLast30daysBrowseFrequency(browseCount);
    }
    
    private void generateUserTags(UserProfile profile) {
        List<String> tags = new ArrayList<>();
        
        // 1. 消费能力标签
        if (profile.getTotalPurchaseAmount().compareTo(new BigDecimal(10000)) > 0) {
            tags.add("高消费");
        } else if (profile.getTotalPurchaseAmount().compareTo(new BigDecimal(5000)) > 0) {
            tags.add("中等消费");
        } else {
            tags.add("低消费");
        }
        
        // 2. 活跃度标签
        if (profile.getLast30daysLoginFrequency() > 20) {
            tags.add("超活跃用户");
        } else if (profile.getLast30daysLoginFrequency() > 10) {
            tags.add("活跃用户");
        } else {
            tags.add("普通用户");
        }
        
        // 3. 购买频次标签
        if (profile.getLast30daysPurchaseFrequency() > 10) {
            tags.add("常客");
        } else if (profile.getLast30daysPurchaseFrequency() > 5) {
            tags.add("稳定客户");
        }
        
        // 4. 品类偏好标签
        if (profile.getPrimaryCategory() != null) {
            tags.add("喜好_" + profile.getPrimaryCategory());
        }
        
        profile.setTags(String.join(",", tags));
    }

    @Override
    public UserProfileAnalysisDTO getUserProfileAnalysis(Long userId) {
        UserProfile profile = getBaseMapper().selectByUserId(userId);
        if (profile == null) {
            return null;
        }
        
        UserProfileAnalysisDTO dto = new UserProfileAnalysisDTO();
        dto.setUserId(userId);
        dto.getConsumptionAnalysis().setTotalPurchaseAmount(profile.getTotalPurchaseAmount());
        dto.getConsumptionAnalysis().setAverageOrderAmount(profile.getAverageOrderAmount());
        dto.getConsumptionAnalysis().setMonthlyPurchaseFrequency(profile.getLast30daysPurchaseFrequency());
        
        dto.getBehaviorAnalysis().setLast30DaysLoginCount(profile.getLast30daysLoginFrequency());
        dto.getBehaviorAnalysis().setLast30DaysBrowseCount(profile.getLast30daysBrowseFrequency());
        //dto.getBehaviorAnalysis().setPreferredLoginTime(profile.getLastLoginTime());
        //dto.getBehaviorAnalysis().setLastPurchaseTime(profile.getLastPurchaseTime());
        
        dto.setTags(Arrays.asList(profile.getTags().split(",")));
        dto.getCategoryPreference().setMostPurchasedCategory(profile.getPrimaryCategory());
        
        return dto;
    }
    
    @Override
    public void batchUpdateUserProfiles() {
        List<Long> userIds = getBaseMapper().selectList(null).stream()
            .map(UserProfile::getUserId)
            .collect(Collectors.toList());
            
        for (Long userId : userIds) {
            try {
                analyzeAndUpdateUserProfile(userId);
            } catch (Exception e) {
                log.error("批量更新用户画像失败，userId: {}", userId, e);
                // 继续处理下一个用户
            }
        }
    }
    
    @Override
    public List<Long> getRecommendedProducts(Long userId) {
        UserProfile profile = getBaseMapper().selectByUserId(userId);
        if (profile == null) {
            return Collections.emptyList();
        }
        
        // 基于用户主要品类和消费能力推荐商品
        ProductCategory primaryCategory = profile.getPrimaryCategory() != null ? 
            ProductCategory.valueOf(profile.getPrimaryCategory()) : null;
            
        if (primaryCategory == null) {
            return Collections.emptyList();
        }
        
        // TODO: 实现具体的推荐逻辑，可以基于用户画像特征进行个性化推荐
        return Collections.emptyList();
    }
}