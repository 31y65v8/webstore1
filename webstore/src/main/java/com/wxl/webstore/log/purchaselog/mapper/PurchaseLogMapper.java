package com.wxl.webstore.log.purchaselog.mapper;

import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品购买记录日志表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
@Mapper
public interface PurchaseLogMapper extends BaseMapper<PurchaseLog> {

    @Select("SELECT DATE_FORMAT(purchase_time, '%Y-%m') as month, SUM(quantity) as total " +
            "FROM purchase_log WHERE product_id = #{productId} " +
            "GROUP BY month ORDER BY month ASC LIMIT 12")
    List<Map<String, Object>> selectMonthlySalesByProductId(@Param("productId") Long productId);

}
