package com.wxl.webstore.recommander.UserCFRecommander.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Spark处理用的推荐结果类
 * 实现Serializable接口以便在Spark集群中传输
 */
public class Recommendation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long userId;          // 用户ID
    private List<Long> itemIds;   // 推荐商品ID列表
    
    // 无参构造器(Spark需要)
    public Recommendation() {}
    
    // 全参构造器
    public Recommendation(Long userId, List<Long> itemIds) {
        this.userId = userId;
        this.itemIds = itemIds;
    }
    
    // Getter和Setter(Spark需要)
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<Long> getItemIds() {
        return itemIds;
    }
    
    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
    
    @Override
    public String toString() {
        return "Recommendation{" +
               "userId=" + userId +
               ", itemIds=" + itemIds +
               '}';
    }
}