/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.R;
import io.renren.modules.user.entity.TokenEntity;

/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ITokenService extends IService<TokenEntity> {
    /**
     * token 查询
     *
     * @param token
     * @return
     */
    TokenEntity queryByToken(String token);

    /**
     * 生成token
     *
     * @param userId 用户ID
     * @return 返回token信息
     */
    TokenEntity createToken(Integer userId, String mobile);

    /**
     * 设置token过期
     *
     * @param userId 用户ID
     */
    void expireToken(Integer userId);

    /**
     * 判断 Token 是否过期
     *
     * @param tokenEntity
     * @return
     */
    boolean isExpire(TokenEntity tokenEntity);

    /**
     * token 有效性校验
     *
     * @param tokenEntity
     * @return
     */
    R checkToken(TokenEntity tokenEntity);
}
