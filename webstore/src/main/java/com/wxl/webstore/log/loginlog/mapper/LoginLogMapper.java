package com.wxl.webstore.log.loginlog.mapper;

import com.wxl.webstore.log.loginlog.entity.LoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户登录登出日志表 Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-24
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

}
