package io.renren.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.exception.RRException;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.NumUtil;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.system.dao.UserSmsRecordDao;
import io.renren.modules.system.entity.SmsAccountEntity;
import io.renren.modules.system.entity.SystemDict;
import io.renren.modules.system.entity.UserSmsRecord;
import io.renren.modules.system.service.ISmsService;
import io.renren.modules.system.service.ISystemDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsServiceImpl extends ServiceImpl<UserSmsRecordDao, UserSmsRecord> implements ISmsService {
    @Autowired
    IRedisService iRedsiService;
    @Autowired
    ISystemDictService systemDictService;
    @Autowired
    UserSmsRecordDao userSmsRecordDao;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 验证码过期时间
     */
    private long expireTime;

    @Override
    public boolean sendCode(String mobile, String ip, Integer type, Boolean sendSmsCode, String defaultSmsCode, SmsAccountEntity smsAccountEntity) {
        // 1 参数校验
        if (!NumUtil.isMobile(mobile))
            throw new RRException(RRExceptionEnum.BAD_REQUEST_PARAMS, "手机号不合法");

        // 2 次数校验 dateStr:mobile
        String dateStr = DateUtils.format(new Date(), StaticConstant.DATE_FORMAT_DATE);
        verifySendNum(dateStr, mobile);

        // 3 发送短信
        String code;
        if (StringUtils.isNotBlank(defaultSmsCode)) {
            code = defaultSmsCode;
        } else {
            code = NumUtil.createRandomNum(6);
        }

        if (!doSmsCodeSend(mobile, ip, type, code, sendSmsCode, smsAccountEntity)) {
            throw new RRException(RRExceptionEnum.SERVER_HANDLE_ERROR, "短信发送异常");
        }

        // 4 更新短信缓存记录
        updateSmsCache(dateStr, mobile);
        return true;
    }

    /**
     * @param dateStr
     * @param mobile
     */
    private void verifySendNum(String dateStr, String mobile) {
        SystemDict sysDict = systemDictService.selectByKey(StaticConstant.SYSTEM_DICT_SMS_CONFIG);
        if (null == sysDict) {
            log.warn("系统参数-短信校验未设置");
        }

        // 已发送短信过期时间
        Long currentCodeExpireTime = iRedsiService.getExpire(RedisCacheKeyConstant.SMS_CODE_PREFIX.concat(mobile), TimeUnit.SECONDS);

        if (null != currentCodeExpireTime && currentCodeExpireTime - 60 > 0) { //60秒内发送，则提示操作频繁
            throw new RRException(RRExceptionEnum.CLIENT_FREQUENT_OPERATION);
        }

        JSONObject parseObject = JSONObject.parseObject(sysDict.getVal());
        int max_num_pre_day = parseObject.getInteger("max_num_pre_day");
        expireTime = parseObject.getLongValue("sms_expire_time_pre_code");
        String cacheNumPerDay = iRedsiService.getHash(RedisCacheKeyConstant.SMS_CODE_COUNT_PREFIX + dateStr, mobile);

        if (StringUtils.isNotBlank(cacheNumPerDay) && Integer.valueOf(cacheNumPerDay).compareTo(max_num_pre_day) >= 0) {
            throw new RRException(RRExceptionEnum.SMS_CODE_BEYOND);
        }
    }

    /**
     * 更新缓存存储
     *
     * @param dateStr
     * @param mobile
     */
    private void updateSmsCache(String dateStr, String mobile) {

        String cacheNumPerDay = iRedsiService.getHash(RedisCacheKeyConstant.SMS_CODE_COUNT, mobile);

        int countNum = StringUtils.isBlank(cacheNumPerDay) ? 0 : Integer.valueOf(cacheNumPerDay);

        iRedsiService.putHashKey(RedisCacheKeyConstant.SMS_CODE_COUNT_PREFIX + dateStr, mobile, ++countNum + "");

    }

    /**
     * @param mobile
     * @param ip
     * @param type
     * @param code
     * @param sendSmsCode
     * @param smsAccountEntity
     * @return
     */

    private boolean doSmsCodeSend(String mobile, String ip, Integer type, String code, Boolean sendSmsCode, SmsAccountEntity smsAccountEntity) {
        if (sendSmsCode) {
            exeSendCodeViaRestTemplate(mobile, code, smsAccountEntity);
        }
        int num = userSmsRecordDao.insert(new UserSmsRecord(mobile, ip, type, code));
        if (num > 0) {
            iRedsiService.set(RedisCacheKeyConstant.SMS_CODE_PREFIX.concat(mobile), code, expireTime, TimeUnit.SECONDS);
        }
        return true;
    }


    private boolean exeSendCodeViaRestTemplate(String mobile, String code, SmsAccountEntity smsAccountEntity) {
        boolean result = true;
        String content = smsAccountEntity.getTemplate().replace("${verifyCode}", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();

        long beginTime = System.currentTimeMillis();
        requestEntity.add("un", smsAccountEntity.getAccount());
        requestEntity.add("pw", smsAccountEntity.getPassword());
        requestEntity.add("phone", mobile);
        requestEntity.add("msg", content);
        requestEntity.add("rd", "1");// 报告状态

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestEntity, headers);
        String response = restTemplate.postForObject(smsAccountEntity.getUrl(), formEntity, String.class);

        // result valid
        log.info(">>> invoke 253 接口响应:[{}] 耗时[{}]s.", response, System.currentTimeMillis() - beginTime);

        return result;
    }

    @Override
    public boolean validCode(String mobile, String code) {
        String codeReal = iRedsiService.getVal(RedisCacheKeyConstant.SMS_CODE_PREFIX.concat(mobile));
        return code.equals(codeReal);
    }

}
