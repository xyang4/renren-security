/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.user.dao.UserDao;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.TokenService;
import io.renren.modules.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
    @Autowired
    private TokenService tokenService;

    @Override
    public UserEntity queryByMobile(String mobile) {
        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", mobile));
    }

    @Override
    public Integer registeredQuickly(String mobile) {
        UserEntity entity = new UserEntity();
        entity.setMobile(mobile);
        entity.setPasswd(DigestUtils.sha256Hex(mobile.substring(mobile.length() - 7)));
        save(entity);
        return entity.getUserId();
    }

    /**
     * 全面校验用户：所有用户状态校验
     */
    public boolean overallCheckUser(UserEntity userEntity){
        //校验用户状态
        if(userEntity.getStatus()!=1){
            return false;
        }
        return true;
    }

}
