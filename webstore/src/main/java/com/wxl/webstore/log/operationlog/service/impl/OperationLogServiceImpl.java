package com.wxl.webstore.log.operationlog.service.impl;

import com.wxl.webstore.log.operationlog.entity.OperationLog;
import com.wxl.webstore.log.operationlog.mapper.OperationLogMapper;
import com.wxl.webstore.log.operationlog.service.OperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-04-25
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void saveOperationLog(OperationLog operationLog) {
        save(operationLog);
    }
}
