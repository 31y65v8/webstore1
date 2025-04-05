package ${package.Entity};

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

<#if table.comment!?length gt 0>
    /**
    * ${table.comment}
    */
</#if>
@Schema(description = "${table.comment!}")
@Getter
@Setter
@TableName("${table.name}")
public class ${entity} implements Serializable {
private static final long serialVersionUID = 1L;

<#list table.fields as field>
    <#if field.comment!?length gt 0>
        @Schema(description = "${field.comment}")
    </#if>
    @TableField("${field.name}")
    private ${field.propertyType} ${field.propertyName};
</#list>
}