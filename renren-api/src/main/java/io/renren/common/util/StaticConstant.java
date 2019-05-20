package io.renren.common.util;

import io.renren.common.utils.Constant;
import lombok.experimental.UtilityClass;

/**
 * 常量
 */
@UtilityClass
public class StaticConstant extends Constant {

    public static final String USER_KEY = "userId";
    public static final String TOKEN_KEY = "token";
    public static final String SIGN_KEY = "sign";
    public static final String MER_KEY = "merId";
    public static final String TIMESTAMP_KEY = "timeStamp";

    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
    public static final String CHARSET_UTF8 = "utf-8";
    public static final String ZEAR_CHAR = "0";
    public static final String PLUS_CHAR = "+";

    public static final int DATA_STATUS_NORMAL = 1;
    public static final int DATA_STATUS_DELETED = 3;


    public static final String SYSTEM_DICT_SMS_CONFIG = "sms_config";

}
