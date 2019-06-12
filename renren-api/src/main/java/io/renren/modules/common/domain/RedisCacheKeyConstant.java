package io.renren.modules.common.domain;

import lombok.experimental.UtilityClass;

/**
 * Redis 存储 Key
 */
@UtilityClass
public class RedisCacheKeyConstant {
    /**
     * 用户可抢订单 list 前缀
     */
    public static final String ORDER_LIST_CAN_BUY_PREFIX = "order_list_can_buy:";
    /**
     * 在线用户前缀:心跳监测
     */
    public static final String ONLINE_USER_PREFIX = "online:user:";
    /**
     * 在线Channel
     */
    public static final String ONLINE_CHANNEL = "online:channel";
    /**
     * 可抢购用户
     */
    public static final String USERS_CAN_RUSH_BUY = "users_can_rush_buy";
    public static final String USERS_CAN_RUSH_BUY_PREFIX = "users_can_rush_buy:";
    public static final String SMS_CODE_PREFIX = "sms_code:";
    public static final String SMS_CODE_COUNT = "sms_code_count";

    public static final String SMS_CODE_COUNT_PREFIX = "sms_code_count:";
    /**
     * 订单锁前缀
     */
    public static final String LOCK_ORDER_PREFIX = "lock:order:";
    /**
     * 已下发清单信息的用户
     */
    public static final String USERS_PUSHED_RUSH_ORDER_PREFIX = "users_pushed_rush_order_prefix:";

}
