package com.wxl.webstore.log.operationlog.mapper;

import com.wxl.webstore.log.operationlog.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-25
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}
