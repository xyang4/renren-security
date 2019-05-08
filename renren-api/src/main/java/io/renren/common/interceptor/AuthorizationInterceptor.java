/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.common.interceptor;


import io.renren.common.annotation.Login;
import io.renren.common.config.RenrenProperties;
import io.renren.common.exception.RRException;
import io.renren.common.util.HttpUtils;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        String requestPath = requestURI.substring(request.getContextPath().length(), requestURI.length());
        log.info("Execute Access Intercept：content-type[{}] method-type[{}] uri[{}] remoteAddr[{}:{}] url[{}].",
                request.getContentType(), request.getMethod(),
                requestURI, HttpUtils.getRemoteAddr(request), request.getRemotePort(), request.getRequestURL());

        Login annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        } else {
            log.info("Request Handle[{}]", handler.getClass().getTypeName());
            return true;
        }

        if (annotation == null) {
            return true;
        }

        // 从header中获取token
        String token = request.getHeader(StaticConstant.TOKEN_KEY);
        // 如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(StaticConstant.TOKEN_KEY);
        }

        // token为空
        if (StringUtils.isBlank(token)) {
            throw new RRException(StaticConstant.TOKEN_KEY + "不能为空");
        }

        TokenService tokenService = SpringContextUtils.getBean(TokenService.class);
        // 查询token信息
        TokenEntity tokenEntity = tokenService.queryByToken(token);

        long tokenExpire = tokenEntity.getExpireTime().getTime();
        long surplusExpire = 0;
        if (tokenEntity == null || (surplusExpire = tokenExpire - System.currentTimeMillis()) < 0) {
            throw new RRException(StaticConstant.TOKEN_KEY + "已失效，请重新登录");
        }

        RenrenProperties renrenProperties = SpringContextUtils.getBean(RenrenProperties.class);
        if (surplusExpire < renrenProperties.getJwtExpire() * 0.25) {
            log.info(">> User[{}] Token[{}] will be Expired[{}s], surplus[{}s]，Begin to reset.", tokenEntity.getMobile(), tokenEntity.getMobile(), renrenProperties.getJwtExpire() * 1000 * 60, surplusExpire / 1000);
            tokenService.createToken(tokenEntity.getUserId(), tokenEntity.getMobile());
        }


        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(StaticConstant.USER_KEY, tokenEntity.getUserId());
        return true;
    }
}