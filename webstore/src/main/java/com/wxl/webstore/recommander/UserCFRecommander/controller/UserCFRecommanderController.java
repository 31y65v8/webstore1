package com.wxl.webstore.recommander.UserCFRecommander.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wxl.webstore.common.utils.JwtUtil;
import com.wxl.webstore.product.dto.ProductDTO;
import com.wxl.webstore.product.service.ProductService;
import com.wxl.webstore.recommander.UserCFRecommander.service.UserCfRecommanderService;
import com.wxl.webstore.common.Result;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/recommand")
public class UserCFRecommanderController {

    @Autowired
    UserCfRecommanderService userCfRecommanderService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    ProductService productService;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未授权的请求");
        }
        token = token.substring(7);
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("解析token失败：" + e.getMessage());
        }
    }

    @GetMapping("/products")
    public Result<List<ProductDTO>> getUserCfRecommandedProducts(
            @RequestParam(value = "topK", defaultValue = "5") int topK,
            @RequestParam(value = "topN", defaultValue = "10") int topN,
            HttpServletRequest request){

                try{
                    Long userId = getUserIdFromToken(request);
                    userCfRecommanderService.loadUserData();
                    List<Long> recommandedProductIds = userCfRecommanderService.recommend(userId, topK, topN);
                    List<ProductDTO> recommandedProductDTOs = productService.getProductsByIdsDTOs(recommandedProductIds);
                    return Result.success(recommandedProductDTOs);
            }catch (Exception e){
                return Result.error("获取推荐商品列表失败：" + e.getMessage());
            }
    }

}
