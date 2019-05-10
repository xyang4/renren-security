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


    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String CHARSET_UTF8 = "utf-8";
    public static final String REDIS_CACHE_KEY_PREFIX_ONLINE = "online";
}
