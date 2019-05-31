package io.renren.modules.user.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.annotation.RequestDataSign;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.system.service.ISmsService;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.form.LoginForm;
import io.renren.modules.user.form.UserInfoForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@Api("用户相关")
@RestController
@RequestMapping("app/user")
public class AppUserController extends BaseController {

    @Autowired
    ISmsService iSmsService;

    @PostMapping("login")
    @ApiOperation("登录")
    @RequestDataSign
    public R loginOrRegister(@RequestBody @Validated LoginForm vo, BindingResult br) {

        // 1 验证码校验
        iSmsService.validCode(vo.getMobile(), vo.getSmsCode());

        // 2 未注册快速注册
        UserEntity userEntity = iUserService.queryByMobile(vo.getMobile());
        Integer userId = null;
        if (null == userEntity) {
            // 快速注册 外部注册功能不开放，后台注册
            //userId = iUserService.registeredQuickly(vo.getMobile());
            return R.error(-1,"用户未注册");
        } else {
            userId = userEntity.getUserId();
        }
        TokenEntity token = iTokenService.createToken(userId, userEntity.getMobile());


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
        iTokenService.expireToken(tokenEntity.getUserId());
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
        user.setNickName(form.getName());
        user.setPasswd(DigestUtils.sha256Hex(form.getPassword()));
        user.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        iUserService.save(user);

        return R.ok();
    }

    @PostMapping("updateNickName")
    @ApiOperation("修改昵称")
    @AppLogin
    public R updateNickName(@RequestBody Map param){
        TokenEntity tokenEntity = getToken();
        if (null != (checkToken(tokenEntity))) {
            return R.error();
        }
        String nickName = (String) param.get("nickName");
        if(StringUtils.isBlank(nickName)){
            return R.error(-10002,"请求参数错误");
        }
        UserEntity updateUser = new UserEntity();
        updateUser.setUserId(tokenEntity.getUserId());
        updateUser.setNickName(nickName);
        iUserService.updateById(updateUser);
        return R.ok();
    }


    @PostMapping("updatePwd")
    @ApiOperation("修改密码")
    @AppLogin
    public R updatePwd(@RequestBody Map param){
        TokenEntity tokenEntity = getToken();
        if (null != (checkToken(tokenEntity))) {
            return R.error();
        }
        UserEntity userEntity = iUserService.getById(tokenEntity.getUserId());
        if(userEntity == null){
            return R.error(-10001,"用户不存在");
        }
        String oldPwd = (String) param.get("oldPwd");
        String newPwd = (String) param.get("newPwd");
        String newConfirmPwd = (String) param.get("newConfirmPwd");
        if(StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd) || StringUtils.isBlank(newConfirmPwd)){
            return R.error(-10002,"请求参数错误");
        }
        if(!DigestUtils.sha256Hex(oldPwd).equals(userEntity.getPasswd())){
            return R.error(-10003,"原密码错误");
        }
        if(!newPwd.equals(newConfirmPwd)){
            return R.error(-10004,"确认密码不一致");
        }
        UserEntity updateUser = new UserEntity();
        updateUser.setUserId(tokenEntity.getUserId());
        updateUser.setPasswd(DigestUtils.sha256Hex(newPwd));
        iUserService.updateById(updateUser);
        return R.ok();
    }

}
