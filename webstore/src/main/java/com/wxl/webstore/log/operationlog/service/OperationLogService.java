package com.wxl.webstore.log.operationlog.service;

import com.wxl.webstore.log.operationlog.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author wxl
 * @since 2025-04-25
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLog operationLog);
}
