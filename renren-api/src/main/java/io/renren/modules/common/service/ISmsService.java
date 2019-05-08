package io.renren.modules.common.service;

public interface ISmsService {
    /**
     * 短信校验
     *
     * @param mobile
     * @param smsCode
     * @return
     */
    boolean validCode(String mobile, String smsCode);
}
