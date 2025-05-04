package com.wxl.webstore.browse.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

    /**
    * 商品浏览记录表
    */
@Schema(description = "商品浏览记录表")
@Getter
@Setter
@TableName("browse")
public class Browse implements Serializable {
private static final long serialVersionUID = 1L;

        @Schema(description = "浏览记录ID")
    @TableField("id")
    private Long id;
        @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
        @Schema(description = "商品ID")
    @TableField("product_id")
    private Long productId;
        @Schema(description = "浏览时间")
    @TableField("time")
    private LocalDateTime time;
        @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
    @Schema(description = "停留时长（秒）")
    @TableField("duration")
    private Integer duration;
}