/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.controller;


import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.common.service.ISmsService;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.form.LoginForm;
import io.renren.modules.user.form.UserInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 登录接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/app")
@Api(tags = "注册登录接口")
public class AppLoginController extends BaseController {

    @Autowired
    ISmsService iSmsService;

    @PostMapping("login")
    @ApiOperation("登录")
    public R loginOrRegister(@RequestBody @Validated LoginForm vo, BindingResult br) {

        // 1 验证码校验
        iSmsService.validCode(vo.getMobile(), vo.getSmsCode());

        // 2 未注册快速注册，
        UserEntity userEntity = userService.queryByMobile(vo.getMobile());
        String userId;
        if (null == userEntity) {
            userId = userService.registeredQuickly(vo.getMobile());
        } else {
            userId = userEntity.getId();
        }
        TokenEntity token = tokenService.createToken(userId, userEntity.getMobile());
        // 3 返回token
        return R.ok(token.getToken());
    }

    @AppLogin
    @PostMapping("logout")
    @ApiOperation("注销")
    public R logout() {
        TokenEntity tokenEntity = getToken();
        R r;
        if (null != (r = checkToken(tokenEntity))) {
            return r;
        }
        tokenService.expireToken(tokenEntity.getUserId());
        return R.ok();
    }

    @PostMapping("update")
    @ApiOperation("密码修改")
    @AppLogin
    public R register(@RequestBody @Validated UserInfoForm form, BindingResult br) {
        TokenEntity tokenEntity = getToken();

        R r;
        if (null != (r = checkToken(tokenEntity))) {
            return r;
        }
        UserEntity user = new UserEntity();
        user.setMobile(form.getMobile());
        user.setName(form.getName());
        user.setPassword(DigestUtils.sha256Hex(form.getPassword()));
        user.setCreateTime(new Date());
        userService.save(user);

        return R.ok();
    }
}
