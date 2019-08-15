package io.renren.modules.common.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.util.HttpUtils;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.form.SmsSubmitForm;
import io.renren.modules.system.service.ISmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("common")
@Api(tags = "通用接口")
public class CommonController extends BaseController {


    @Autowired
    ISmsService iSmsService;

    //    @RequestDataSign
    @PostMapping("ssm/sendCode")
    @ApiOperation("发送短信")
    @ResponseBody
    public R sendCode(@Validated @RequestBody SmsSubmitForm form, BindingResult br) {
        boolean rFlag;
        if (null != form.getType() || form.getType() == 1) {
            // 图形验证码校验
            String originKaptcha = iRedisService.getVal(form.getMobile());
            if (StringUtils.isBlank(originKaptcha) || !StringUtils.equalsAnyIgnoreCase(form.getKaptcha(), originKaptcha)) {
                return R.error(RRExceptionEnum.BAD_REQUEST_PARAMS, "无效的图形验证码");
            }

          /*  SmsAccountEntity accountEntity = new SmsAccountEntity(
                    renrenProperties.getSmsUrl(),
                    renrenProperties.getSmsAccount(),
                    renrenProperties.getSmsPassword(),
                    renrenProperties.getSmsTemplate()
            );*/
            String clientIp = HttpUtils.getIp();
            rFlag = iSmsService.sendCode(form.getMobile(), clientIp, form.getType(), renrenProperties.isSmsSendOpen(),
                    renrenProperties.getSmsCodeDefault(), null);
            return R.ok(rFlag);
        } else {
            return R.error(RRExceptionEnum.BAD_REQUEST_PARAMS, "暂不支持的短信类型");
        }
    }

    @Autowired
    DefaultKaptcha defaultKaptcha;

    @GetMapping(value = "kaptcha", produces = "image/jpeg")
    @ApiOperation("获取图形验证码")
    public void createKaptcha(String uniqueKey, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // 生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute("rightCode", createText);
            // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
            iRedisService.set(uniqueKey, createText, renrenProperties.getJwtExpire(), TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 定义response 输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] captchaChallengeAsJpeg = captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }


}
