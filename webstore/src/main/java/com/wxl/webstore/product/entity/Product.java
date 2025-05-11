package com.wxl.webstore.product.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wxl.webstore.common.enums.ProductCategory;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableLogic;

    /**
    * 商品信息表
    */
@Schema(description = "商品信息表")
@Getter
@Setter
@TableName("product")
public class Product implements Serializable {
private static final long serialVersionUID = 1L;

        @Schema(description = "商品ID(主键)")
    @TableField("id")
    @TableId("id")
    private Long id;
        @Schema(description = "商品名称")
    @TableField("name")
    private String name;
        @Schema(description = "商品价格(精确到分)")
    @TableField("price")
    private BigDecimal price;
        @Schema(description = "库存数量")
    @TableField("storage")
    private Integer storage;
        @Schema(description = "商品图片URL")
    @TableField("imgurl")
    private String imgurl;
        @Schema(description = "商品详细描述")
    @TableField("description")
    private String description;
        @Schema(description = "商品分类")
    @TableField("category")
    private ProductCategory category;
        @Schema(description = "卖家ID(关联user表)")
    @TableField("seller_id")
    private Long sellerId;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
        @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
        @Schema(description = "是否删除(0-正常,1-删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
    @Schema(description = "商品销量")
    @TableField("sales")
    private Integer sales;
}