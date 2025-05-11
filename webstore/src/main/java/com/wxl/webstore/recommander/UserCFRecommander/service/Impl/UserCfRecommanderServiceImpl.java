package com.wxl.webstore.recommander.UserCFRecommander.service.Impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List; 
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxl.webstore.log.browse.entity.Browse;
import com.wxl.webstore.log.browse.service.BrowseService;
import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;
import com.wxl.webstore.recommander.UserCFRecommander.service.UserCfRecommanderService;

@Service
public class UserCfRecommanderServiceImpl implements UserCfRecommanderService{

    private static final Logger logger = LoggerFactory.getLogger(UserCfRecommanderServiceImpl.class);


    @Autowired
    BrowseService browseService;

    @Autowired
    PurchaseLogService purchaseLogService;

    // 用户 -> 物品集合（可以是浏览或购买）
    //private Map<Long, Set<Long>> userItemMap = new HashMap<>();
    // 评分矩阵：userId -> { productId -> score }
    private Map<Long, Map<Long, Double>> userRatingMap = new HashMap<>();

    /*初始化行为数据（从数据库加载）*/
    public void loadUserData() {

        logger.info("开始加载用户行为数据...");

        List<Browse> browseList = browseService.list(
            new QueryWrapper<Browse>().ge("time", LocalDateTime.now().minusDays(30))
        );
        logger.info("加载到 {} 条浏览记录", browseList.size());
        
        List<PurchaseLog> purchaseList = purchaseLogService.list(
            new QueryWrapper<PurchaseLog>().ge("purchase_time", LocalDateTime.now().minusDays(30))
        );
        logger.info("加载到 {} 条购买记录", purchaseList.size());

        // 合并评分：duration + quantity * 权重
    for (Browse b : browseList) {
        userRatingMap
            .computeIfAbsent(b.getUserId(), k -> new HashMap<>())
            .merge(b.getProductId(), b.getDuration().doubleValue(), Double::sum);
    }

    for (PurchaseLog p : purchaseList) {
        userRatingMap
            .computeIfAbsent(p.getUserId(), k -> new HashMap<>())
            .merge(p.getProductId(), p.getQuantity() * 5.0, Double::sum); // 权重5可调
    }

    logger.info("用户评分数据加载完成，共 {} 个用户", userRatingMap.size());
    }

    /*主函数：推荐商品*/
    public List<Long> recommend(Long targetUserId, int topK, int topN) {
        //Set<Long> targetItems = userItemMap.getOrDefault(targetUserId, Collections.emptySet());
        logger.info("为用户 {} 生成推荐，TopK={}, TopN={}", targetUserId, topK, topN);

        Map<Long, Double> targetRatings = userRatingMap.getOrDefault(targetUserId, Collections.emptyMap());
        if (targetRatings.isEmpty()) {
        logger.warn("用户 {} 无评分记录，返回空推荐列表", targetUserId);
        return Collections.emptyList();
        }

        // 计算用户相似度
        Map<Long, Double> simMap = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : userRatingMap.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(targetUserId)) continue;

            double sim = cosineSimilarity(targetRatings, entry.getValue());
            if (sim > 0) simMap.put(otherUserId, sim);
        }
        logger.info("计算得到 {} 个相似用户", simMap.size());

        // 选 Top-K 相似用户
        List<Map.Entry<Long, Double>> topKUsers = simMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topK)
            .collect(Collectors.toList());

        logger.debug("TopK 用户：{}", topKUsers);

        // 推荐集合：商品 -> 加权得分
        Map<Long, Double> itemScoreMap = new HashMap<>();
        for (Map.Entry<Long, Double> simEntry : topKUsers) {
            Long simUserId = simEntry.getKey();
            Double simScore = simEntry.getValue();

        for (Map.Entry<Long, Double> productEntry : userRatingMap.get(simUserId).entrySet()) {
            Long itemId = productEntry.getKey();
            Double score = productEntry.getValue();
            if (!targetRatings.containsKey(itemId)) {
                itemScoreMap.merge(itemId, simScore * score, Double::sum);
            }
        }
    }
    logger.info("推荐候选商品数量：{}", itemScoreMap.size());

        // 推荐 Top-N 商品
        List<Long> recommendList = itemScoreMap.entrySet().stream()
        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
        .limit(topN)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    logger.info("为用户 {} 推荐商品：{}", targetUserId, recommendList);
    return recommendList;
    }


    /*Jaccard 相似度计算
    private double jaccard(Set<Long> set1, Set<Long> set2) {
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }*/

    /* 余弦相似度计算 */ 
    private double cosineSimilarity(Map<Long, Double> vec1, Map<Long, Double> vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (Map.Entry<Long, Double> entry : vec1.entrySet()) {
            Long key = entry.getKey();
            double val1 = entry.getValue();
            double val2 = vec2.getOrDefault(key, 0.0);
            dot += val1 * val2;
            norm1 += val1 * val1;
        }

        for (double val2 : vec2.values()) {
            norm2 += val2 * val2;
        }

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
}

    
}
