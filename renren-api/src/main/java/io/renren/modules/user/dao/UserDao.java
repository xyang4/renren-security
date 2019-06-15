/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    Map<String, Object> getAccountBaseInfo(@Param("userId") Integer userId, @Param("mobile") String mobile);
}
