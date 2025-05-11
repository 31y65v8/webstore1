package com.wxl.webstore.user.mapper;

import com.wxl.webstore.user.entity.User;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

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


    @Update("UPDATE user SET is_deleted = 1 WHERE id = #{id}")
    int updateIsDeleteById(@Param("id") Long id);

    @Select("SELECT id FROM users")
    List<Long> findAllUserIds();



}
