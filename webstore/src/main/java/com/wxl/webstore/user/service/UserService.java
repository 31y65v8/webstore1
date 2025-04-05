package com.wxl.webstore.user.service;

import com.wxl.webstore.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.user.dto.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.wxl.webstore.common.enums.UserRole;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wxl
 * @since 2025-03-30
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param dto 用户注册DTO
     * @return 注册成功的用户信息
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto 用户登录DTO
     * @return 登录成功的用户信息
     */
    User login(UserLoginDTO dto);

    //UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void deleteByAccountAndRole(String account, UserRole role);
}