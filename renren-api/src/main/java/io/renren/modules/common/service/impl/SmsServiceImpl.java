package io.renren.modules.common.service.impl;

import io.renren.modules.common.service.ISmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements ISmsService {
    @Override
    public boolean validCode(String mobile, String smsCode) {
        // TODO
        return false;
    }
}
