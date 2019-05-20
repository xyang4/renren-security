package io.renren.modules.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 短信发送账户相关
 */
@Data
@AllArgsConstructor
public class SmsAccountEntity {
    private String url;
    private String account;
    private String password;
    private String template;
}
