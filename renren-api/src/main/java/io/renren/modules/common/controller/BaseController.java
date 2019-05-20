package io.renren.modules.common.controller;

import io.renren.common.config.RenrenProperties;
import io.renren.common.util.HttpUtils;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.R;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.ITokenService;
import io.renren.modules.user.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    public ITokenService iTokenService;
    @Autowired
    public IUserService iUserService;

    @Autowired
    RenrenProperties renrenProperties;

    /**
     * 获取当前用户的 token
     *
     * @return
     */
    public TokenEntity getToken() {
        TokenEntity tokenEntity = null;
        String token = HttpUtils.getRequest().getHeader(StaticConstant.TOKEN_KEY);

        if (StringUtils.isNotBlank(token)) {
            tokenEntity = iTokenService.queryByToken(token);
        }
        return tokenEntity;
    }

    /**
     * token 校验
     *
     * @param tokenEntity
     * @return
     */
    public R checkToken(TokenEntity tokenEntity) {
        return iTokenService.checkToken(tokenEntity);
    }
}
