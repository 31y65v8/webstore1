package com.wxl.webstore.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wxl.webstore.user.entity.User;

@Service
public interface AdminService {
    void addSeller(User newSeller);//增加销售人员ID
    void removeSeller(Long sellerId);//删除销售人员ID
    void updateSellerPassword(Long sellerid,String newpassword);//更新销售人员口令
    List<User> getAllSellers();//获取所有销售人员信息
    
    //各商品类别销售业绩查询（根据销售人员筛选）
    //
}
