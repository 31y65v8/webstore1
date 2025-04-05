package com.wxl.webstore.receiverInfo.controller;

import com.wxl.webstore.receiverInfo.service.ReceiverInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


/**
 * <p>
 * 收货地址信息表（级联软删除） 前端控制器
 * </p>
 *
 * @author wxl
 * @since 2025-04-02
 */
@RestController
@RequestMapping("/receiverInfo")
public class ReceiverInfoController {
    @Autowired
    private ReceiverInfoService receiverInfoService;

    // 新建收货信息
    @PostMapping("/create")
    public String createReceiverInfo(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String receiverName = request.get("receiverName").toString();
        String receiverPhone = request.get("receiverPhone").toString();
        String provinceCode = request.get("provinceCode").toString();
        String provinceName = request.get("provinceName").toString();
        String cityCode = request.get("cityCode").toString();
        String cityName = request.get("cityName").toString();
        String districtCode = request.get("districtCode").toString();
        String districtName = request.get("districtName").toString();
        String detail = request.get("detail").toString();
        String label = request.get("label") == null ? "" : request.get("label").toString();

        boolean result = receiverInfoService.createReceiverInfo(userId, receiverName, receiverPhone,
                provinceCode, provinceName, cityCode, cityName, districtCode, districtName, detail, label);
        return result ? "收货信息创建成功" : "收货信息创建失败";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReceiverInfo(@PathVariable Long id) {
        boolean result = receiverInfoService.deleteReceiverInfo(id);
        return result ? "删除成功" : "删除失败，收货信息不存在或删除失败";
    }

}
