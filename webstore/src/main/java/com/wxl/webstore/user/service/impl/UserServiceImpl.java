package com.wxl.webstore.user.service.impl;

import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.mapper.UserMapper;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.user.dto.*;
import com.wxl.webstore.common.enums.UserRole;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxl
 * @since 2025-03-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(UserRegisterDTO dto) {
        // 1. 校验用户名/手机/邮箱是否已存在
        /*if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }*/
        if (dto.getPhone() == null && dto.getEmail() == null) {
            throw new RuntimeException("手机号和邮箱不能同时为空");
        }

        if (dto.getPhone() != null && dto.getRole() !=null && userMapper.selectByPhoneAndRole(dto.getPhone(),dto.getRole()) != null) {
            throw new RuntimeException("手机号已注册");
        }
        if (dto.getEmail() != null && dto.getRole() !=null && userMapper.selectByEmailAndRole(dto.getEmail(),dto.getRole()) != null) {
            throw new RuntimeException("邮箱已注册");
        }


        // 2. 密码加密
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole()); // 从前端获得角色
        user.setRegisterPhone(dto.getPhone());
        user.setRegisterEmail(dto.getEmail());
        user.setRegisterTime(LocalDateTime.now());
        //user.setIsDeleted(false); // 设置初始未删除状态

        // 3. 保存用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO dto) {
        // 1. 根据手机/邮箱查询用户
        User user = null;
        // 判断输入是否为邮箱
        if (dto.getAccount().contains("@")) {
            user = userMapper.selectByEmailAndRole(dto.getAccount(),dto.getRole());
        }
        // 判断输入是否为纯数字（手机号）
        else if (dto.getAccount().matches("\\d+")) {
            user = userMapper.selectByPhoneAndRole(dto.getAccount(),dto.getRole());
        } else {
            throw new RuntimeException("请输入正确的手机号或邮箱");
        }
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }//防止暴力猜测某一号码是否注册过

        return user;
    }



    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // 从SecurityContext中获取当前请求的角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UsernameNotFoundException("未找到认证信息");
        }

        // 获取角色信息
        String roleStr = authentication.getAuthorities().iterator().next().getAuthority();
        UserRole role = UserRole.valueOf(roleStr.replace("ROLE_", ""));

        // 根据账号类型（手机/邮箱）和角色查询用户
        User user = null;
        if (account.contains("@")) {
            user = userMapper.selectByEmailAndRole(account, role);
        } else if (account.matches("\\d+")) {
            user = userMapper.selectByPhoneAndRole(account, role);
        }

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    //用户注销当前登录的账户，软删除
    @Override
    public void deleteByAccountAndRole(String account, UserRole role) {
        User user = null;
        if (account.contains("@")) {
            user = userMapper.selectByEmailAndRole(account, role);
        } else if (account.matches("\\d+")) {
            user = userMapper.selectByPhoneAndRole(account, role);
        }
        
        if (user != null) {
            user.setIsDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

}
