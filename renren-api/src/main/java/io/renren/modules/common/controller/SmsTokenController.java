package io.renren.modules.common.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.annotation.RequestDataSign;
import io.renren.common.config.RenrenProperties;
import io.renren.common.util.HttpUtils;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.form.SmsSubmitForm;
import io.renren.modules.system.entity.SmsAccountEntity;
import io.renren.modules.system.service.ISmsService;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AppLogin
@RestController
@RequestMapping("app/sms")
@Api("token短信发送相关")
public class SmsTokenController extends BaseController {
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    ISmsService iSmsService;

    @AppLogin
    @RequestDataSign
    @PostMapping("/sendCodeByToken")
    @ApiOperation("token发送短信")
    public R sendCodeByToken(@RequestBody SmsSubmitForm form) {
        SmsAccountEntity accountEntity = new SmsAccountEntity(
                renrenProperties.getSmsUrl(),
                renrenProperties.getSmsAccount(),
                renrenProperties.getSmsPassword(),
                renrenProperties.getSmsTemplate()
        );
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        String mobile = tokenEntity.getMobile();
        String clientIp = HttpUtils.getIp();
        boolean flag = iSmsService.sendCode(mobile, clientIp, form.getType(), renrenProperties.isSmsSendOpen(),
                renrenProperties.getSmsCodeDefault(), accountEntity);
        return R.ok(flag);
    }
}
