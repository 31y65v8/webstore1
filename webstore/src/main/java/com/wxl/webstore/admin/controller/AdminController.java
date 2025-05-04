package com.wxl.webstore.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wxl.webstore.admin.service.AdminService;
import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.log.operationlog.annotation.OperationLogAnnoce;
import com.wxl.webstore.user.entity.User;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/addSeller")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogAnnoce(module = "管理员模块", operation = "添加卖家")
    public Result<Void> addSeller(@RequestBody User newSeller) {
        adminService.addSeller(newSeller);
        return Result.success(null);
    }

    @PostMapping("/updateSellerPassword")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogAnnoce(module = "管理员模块", operation = "更新卖家口令")
    public Result<Void> updateSellerPassword(@RequestParam Long userId,@RequestParam String newpassword) {
        adminService.updateSellerPassword(userId, newpassword);
        return Result.success(null);
    }

    @PostMapping("/removeSeller")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogAnnoce(module = "管理员模块", operation = "删除卖家")
    public Result<Void> removeSeller(@RequestParam Long userId) {
        adminService.removeSeller(userId);
        return Result.success(null);
    }

    @GetMapping("/getAllSellers")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogAnnoce(module = "管理员模块", operation = "获取所有卖家")
    public Result<List<User>> getAllSellers() {
        List<User> sellers = adminService.getAllSellers();
        return Result.success(sellers);
    }
}
