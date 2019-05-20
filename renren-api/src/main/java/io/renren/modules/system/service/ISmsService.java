package io.renren.modules.system.service;

import io.renren.modules.system.entity.SmsAccountEntity;

public interface ISmsService {
    /**
     * 短信校验
     *
     * @param mobile
     * @param smsCode
     * @return
     */
    boolean validCode(String mobile, String smsCode);

    boolean sendCode(String mobile, String ip, Integer type, Boolean sendSmsCode, String defaultSmsCode, SmsAccountEntity smsAccountEntity);

}
