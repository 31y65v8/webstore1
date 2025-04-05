package com.wxl.webstore.user.service.impl;

import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.wxl.webstore.common.enums.UserRole;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
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
}