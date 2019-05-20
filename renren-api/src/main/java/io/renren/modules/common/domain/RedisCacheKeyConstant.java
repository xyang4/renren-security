package io.renren.modules.common.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisCacheKeyConstant {
    /**
     * 用户可抢订单 list 前缀
     */
    public static final String ORDER_LIST_CAN_BUY_PREFIX = "order_list_can_buy:";
    /**
     * 在线用户前缀:心跳监测
     */
    public static final String ONLINE_PREFIX = "online:";
    /**
     * 可抢购用户
     */
    public static final String USERS_CAN_RUSH_BUY = "users_can_rush_buy";
    public static final String SMS_CODE_PREFIX = "sms_code:";
    public static final String SMS_CODE_COUNT = "sms_code_count";
    public static final String SMS_CODE_COUNT_PREDIX = "sms_code_count:";
}
