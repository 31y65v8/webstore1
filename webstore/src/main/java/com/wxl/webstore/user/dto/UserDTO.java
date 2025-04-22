package com.wxl.webstore.user.dto;

import com.wxl.webstore.user.entity.User;

import lombok.Data;

@Data
public class UserDTO {
    // 使用String类型存储ID，避免JavaScript精度问题
    private String id;
    private String username;
    private String role;
    private String registerPhone;
    private String registerEmail;
    private String registerTime;
    
    // 可以添加其他需要传给前端的字段，但不包含敏感信息如密码
    
    // 从User实体转换为UserDTO的静态方法
    public static UserDTO fromEntity(User user) {
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.setId(String.valueOf(user.getId()));
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().toString());
        dto.setRegisterPhone(user.getRegisterPhone());
        dto.setRegisterEmail(user.getRegisterEmail());
        
        if (user.getRegisterTime() != null) {
            dto.setRegisterTime(user.getRegisterTime().toString());
        }
        
        return dto;
    }
}
