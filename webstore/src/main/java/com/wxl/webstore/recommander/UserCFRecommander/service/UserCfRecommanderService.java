package com.wxl.webstore.recommander.UserCFRecommander.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxl.webstore.log.browse.entity.Browse;
import com.wxl.webstore.log.browse.service.BrowseService;
import com.wxl.webstore.log.purchaselog.entity.PurchaseLog;
import com.wxl.webstore.log.purchaselog.service.PurchaseLogService;

import java.util.*;
import java.util.stream.Collectors;


public interface UserCfRecommanderService {

    void loadUserData();
    List<Long> recommend(Long targetUserId, int topK, int topN);

}