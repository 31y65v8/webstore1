package com.wxl.webstore.user_profile.mapper;

import com.wxl.webstore.user_profile.entity.UserProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wxl
 * @since 2025-04-29
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    UserProfile selectByUserId(Long userId);

    //void insertOrUpdate(UserProfile profile);
    void insertOrUpdate(@Param("userProfile") UserProfile userProfile);

}
