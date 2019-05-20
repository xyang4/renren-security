package io.renren.modules.common.controller;

import io.renren.common.annotation.RequestDataSign;
import io.renren.common.config.RenrenProperties;
import io.renren.common.util.HttpUtils;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.form.SmsSubmitForm;
import io.renren.modules.system.entity.SmsAccountEntity;
import io.renren.modules.system.service.ISmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("common/sms")
@Api("短信发送相关")
public class SmsController extends BaseController {
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    ISmsService iSmsService;

    @RequestDataSign
    @PostMapping("sendCode")
    @ApiOperation("发送短信")
    public R sendCode(@RequestBody SmsSubmitForm form) {


        SmsAccountEntity accountEntity = new SmsAccountEntity(
                renrenProperties.getSmsUrl(),
                renrenProperties.getSmsAccount(),
                renrenProperties.getSmsPassword(),
                renrenProperties.getSmsTemplate()
        );


        String clientIp = HttpUtils.getIp();


        boolean flag = iSmsService.sendCode(form.getMobile(), clientIp, form.getType(), renrenProperties.isSmsSendOpen(),
                renrenProperties.getSmsCodeDefault(), accountEntity);
        return R.ok(flag);
    }
}
