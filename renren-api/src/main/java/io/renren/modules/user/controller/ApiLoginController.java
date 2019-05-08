/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.controller;


import io.renren.common.annotation.Login;
import io.renren.common.util.StaticConstant;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/api")
@Api(tags = "注册登录接口")
public class ApiLoginController extends BaseController {

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

    @Login
    @PostMapping("logout")
    @ApiOperation("退出")
    public R logout() {
        TokenEntity tokenEntity = getToken();
        tokenService.expireToken(tokenEntity.getUserId());
        Map<String, Object> rMap = new HashMap<>(2);
        rMap.put(StaticConstant.TOKEN_KEY, tokenEntity.getToken());
        rMap.put("expire", tokenEntity.getExpireTime());
        return R.ok(tokenEntity.getToken());
    }

    @PostMapping("update")
    @ApiOperation("密码修改")
    public R register(@RequestBody @Validated UserInfoForm form, BindingResult br) {

        UserEntity user = new UserEntity();
        user.setMobile(form.getMobile());
        user.setName(form.getName());
        user.setPassword(DigestUtils.sha256Hex(form.getPassword()));
        user.setCreateTime(new Date());
        userService.save(user);

        return R.ok();
    }
}
