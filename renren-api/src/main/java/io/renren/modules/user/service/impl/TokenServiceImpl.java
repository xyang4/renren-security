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
import io.renren.common.config.RenrenProperties;
import io.renren.common.util.StaticConstant;
import io.renren.modules.user.dao.TokenDao;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service("tokenService")
public class TokenServiceImpl extends ServiceImpl<TokenDao, TokenEntity> implements TokenService {

    @Autowired
    RenrenProperties renrenProperties;

    @Override
    public TokenEntity queryByToken(String token) {
        return this.getOne(new QueryWrapper<TokenEntity>().eq(StaticConstant.TOKEN_KEY, token));
    }

    @Override
    public TokenEntity createToken(String userId, String mobile) {
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + renrenProperties.getJwtExpire() * 60 * 1000);

        //生成token
        String token = generateToken();

        //保存或更新用户token
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setMobile(mobile);
        tokenEntity.setToken(token);
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(expireTime);
        this.saveOrUpdate(tokenEntity);

        return tokenEntity;
    }

    @Override
    public void expireToken(String userId) {
        Date now = new Date();

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setUpdateTime(now);
        tokenEntity.setExpireTime(now);
        this.saveOrUpdate(tokenEntity);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
