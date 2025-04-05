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
        // 1. 校验account是否已存在
        if (dto.getAccount() == null || dto.getAccount().isEmpty()) {
            throw new RuntimeException("账号不能为空");
        }

        // 判断是手机号还是邮箱
        boolean isEmail = dto.getAccount().contains("@");
        User existingUser;
        
        if (isEmail) {
            existingUser = userMapper.selectByEmailAndRole(dto.getAccount(), dto.getRole());
            if (existingUser != null) {
                throw new RuntimeException("该邮箱已注册");
            }
        } else {
            existingUser = userMapper.selectByPhoneAndRole(dto.getAccount(), dto.getRole());
            if (existingUser != null) {
                throw new RuntimeException("该手机号已注册");
            }
        }

        // 2. 创建用户实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        
        // 根据account类型设置手机号或邮箱
        if (isEmail) {
            user.setRegisterEmail(dto.getAccount());
        } else {
            user.setRegisterPhone(dto.getAccount());
        }
        
        user.setRegisterTime(LocalDateTime.now());

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