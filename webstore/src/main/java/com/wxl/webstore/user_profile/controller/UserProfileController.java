package com.wxl.webstore.user_profile.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wxl.webstore.common.response.Result;
import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.product.entity.Product;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.user.mapper.UserMapper;
import com.wxl.webstore.user.service.UserService;
import com.wxl.webstore.user_profile.entity.UserProfile;
import com.wxl.webstore.user_profile.service.UserProfileAnalysisService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/api/userProfile")
public class UserProfileController {

    @Autowired
    private UserProfileAnalysisService userProfileService;

    @Autowired
    private UserMapper userMapper;

    
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/all")
    public Result<List<UserProfile>> getAllUserProfiles() {
        List<Long> userIds = userMapper.findAllUserIds();
        for(Long userId : userIds){
            userProfileService.analyzeAndUpdateUserProfile(userId);
        }
        
        List<UserProfile> list = userProfileService.list();
        return Result.success(list);
    }

    /*@GetMapping("/recommand")
    public Result<List<Product>> getRecommandProduct(HttpServletRequest httpRequest){
        // 从JWT获取用户ID
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.error(401, "未授权的请求");
        }
        token = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<Long> productIds = userProfileService.getRecommendedProducts(userId);
        List<Product> products = productService.getProductsByIds(productIds);
        return Result.success(products);
    }*/

}
