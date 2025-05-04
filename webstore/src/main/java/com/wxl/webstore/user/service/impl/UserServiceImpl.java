package com.wxl.webstore.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxl.webstore.common.enums.UserRole;
import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.mapper.UserMapper;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.user.dto.UserLoginDTO;
import com.wxl.webstore.user.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserRegisterDTO dto) {
        // 检查用户是否已存在
        if (existsByAccountAndRole(dto.getAccount(), dto.getRole())) {
            throw new RuntimeException("该账号已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setRegisterTime(LocalDateTime.now());
        user.setIsDeleted(false);

        // 设置注册方式（邮箱或手机）
        if (dto.getAccount().contains("@")) {
            user.setRegisterEmail(dto.getAccount());
        } else {
            user.setRegisterPhone(dto.getAccount());
        }

        // 保存用户
        save(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据账号类型（邮箱或手机）和角色查询
        if (dto.getAccount().contains("@")) {
            queryWrapper.eq(User::getRegisterEmail, dto.getAccount());
        } else {
            queryWrapper.eq(User::getRegisterPhone, dto.getAccount());
        }
        queryWrapper.eq(User::getRole, dto.getRole());
        queryWrapper.eq(User::getIsDeleted, false);

        // 查询用户
        User user = getOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    @Transactional
    public void deleteByAccountAndRole(String account, UserRole role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (account.contains("@")) {
            queryWrapper.eq(User::getRegisterEmail, account);
        } else {
            queryWrapper.eq(User::getRegisterPhone, account);
        }
        queryWrapper.eq(User::getRole, role);
        
        User user = getOne(queryWrapper);
        if (user != null) {
            user.setIsDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            updateById(user);
        }
    }

    @Override
    public boolean existsByAccountAndRole(String account, UserRole role) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (account.contains("@")) {
            queryWrapper.eq(User::getRegisterEmail, account);
        } else {
            queryWrapper.eq(User::getRegisterPhone, account);
        }
        queryWrapper.eq(User::getRole, role);
        queryWrapper.eq(User::getIsDeleted, false);
        
        return count(queryWrapper) > 0;
    }

    @Override
    public User getUserInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public String getUserContactInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 优先返回邮箱，如果邮箱为空则返回手机号
        if (user.getRegisterEmail() != null && !user.getRegisterEmail().trim().isEmpty()) {
            return user.getRegisterEmail();
        } else if (user.getRegisterPhone() != null && !user.getRegisterPhone().trim().isEmpty()) {
            return user.getRegisterPhone();
        } else {
            return "未提供联系方式";
        }
    }

    @Override
    public List<User> getAllSellers() {
        return list(new LambdaQueryWrapper<User>().eq(User::getRole, UserRole.SELLER));
    }

    
    @Override
    public void addSeller(User newSeller) {
        newSeller.setRole(UserRole.SELLER);
        //newSeller.setIsDeleted(false);
        //newSeller.setRegisterTime(LocalDateTime.now());
        save(newSeller);
    }

    @Override
    public void removeSeller(Long id) {
        User seller = getById(id);
        seller.setIsDeleted(true);
        seller.setDeletedAt(LocalDateTime.now());
        updateById(seller);
    }

    @Override
    public void updateSellerPassword(Long id, String newPassword) {
        User seller = getById(id);
        seller.setPassword(passwordEncoder.encode(newPassword));
        updateById(seller);
    }
}