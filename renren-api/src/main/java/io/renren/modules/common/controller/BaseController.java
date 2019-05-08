package io.renren.modules.common.controller;

import io.renren.common.config.RenrenProperties;
import io.renren.common.util.HttpUtils;
import io.renren.common.util.StaticConstant;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.TokenService;
import io.renren.modules.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    protected UserService userService;
    @Autowired
    protected TokenService tokenService;
    @Autowired
    RenrenProperties renrenProperties;

    public TokenEntity getToken() {
        TokenEntity tokenEntity = null;
        String token = HttpUtils.getRequest().getHeader(StaticConstant.TOKEN_KEY);
        if (StringUtils.isNotBlank(token)) {
            tokenEntity = tokenService.queryByToken(token);
        }
        return tokenEntity;
    }
}
