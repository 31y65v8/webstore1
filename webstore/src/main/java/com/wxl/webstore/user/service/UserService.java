package com.wxl.webstore.user.service;

import com.wxl.webstore.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.user.dto.*;

import java.util.List;

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
     * @param dto 注册信息
     * @return 注册成功的用户信息
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息
     * @return 登录成功的用户信息
     */
    User login(UserLoginDTO dto);

    //UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 根据账号和角色删除用户
     * @param account 账号
     * @param role 角色
     */
    void deleteByAccountAndRole(String account, UserRole role);

    /**
     * 检查用户是否存在
     * @param account 账号
     * @param role 角色
     * @return 是否存在
     */
    boolean existsByAccountAndRole(String account, UserRole role);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(Long userId);

    //获取用户联系方式
    String getUserContactInfo(Long userId);

    //获取所有卖家
    List<User> getAllSellers();

    

    //添加卖家
    void addSeller(User newSeller);

    //更新卖家口令
    void updateSellerPassword(Long id, String newPassword);

    //删除卖家
    void removeSeller(Long id);
}