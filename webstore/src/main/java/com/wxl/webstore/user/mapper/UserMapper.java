package com.wxl.webstore.user.mapper;

import com.wxl.webstore.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.wxl.webstore.common.enums.UserRole;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-03-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUsernameAndRole(String username, UserRole role);
    User selectByPhoneAndRole(String phone, UserRole role);
    User selectByEmailAndRole(String email, UserRole role);
    //User selectByRole(UserRole role);
    User selectSELLERByIsDeleted(Integer isDeleted);
    User selectCUSTOMERByIsDeleted(Integer isDeleted);
    User selectByUserId(Long userId);


}
