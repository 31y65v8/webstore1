package com.wxl.webstore.admin.service.impl;

import com.wxl.webstore.admin.service.AdminService;
import com.wxl.webstore.user.entity.User;
import com.wxl.webstore.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    UserService userService;

    
    @Override
    public List<User> getAllSellers() {
        return userService.getAllSellers();
    }

    @Override
    public void removeSeller(Long id) {
        User seller = userService.getById(id);
        seller.setIsDeleted(true);
        seller.setDeletedAt(LocalDateTime.now());
    }

    
    @Override
    public void addSeller(User newSeller) {
        userService.addSeller(newSeller);
    }

    @Override
    public void updateSellerPassword(Long userId,String newpassword) {
        userService.updateSellerPassword(userId, newpassword);
    }
}
