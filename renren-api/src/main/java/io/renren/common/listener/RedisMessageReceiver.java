package io.renren.common.listener;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.utils.R;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.domain.RushOrderInfo;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * redis 消息队列监听器，//TODO 根据业务指定不同的消费者
 */
@Slf4j
@Component
public class RedisMessageReceiver implements MessageListener {
    @Autowired
    IRedisService iRedisService;
    @Autowired
    INettyService iNettyService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        String m = message.toString();
        log.info("Redis 队列消费[{}]开始：Message[{}].", topic, m);
        if (!m.startsWith("{") || !m.endsWith("}")) {
            log.warn("消息格式错误");
            return;
        }

        RedisMessageDomain messageDomain = JSONObject.parseObject(m, RedisMessageDomain.class);
        if (null == messageDomain || null == messageDomain.getTopic() || null == messageDomain.getContent()) {
            log.warn("Redis 队列消费[{}]完成!! {}", topic, RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR.getMsg());
            return;
        }

        R r = null;
        WebSocketActionTypeEnum actionTypeEnum = WebSocketActionTypeEnum.getByCode(topic);
        switch (actionTypeEnum) {
            case BEGIN_RECEIPT: // 1 追加用户至可抢订单的用户集合中, 定时从该集合便历用户，并进行订单推送?
                JSONObject jsonObject = JSONObject.parseObject(messageDomain.getContent());
                String mobile = jsonObject.getString("mobile");
                Integer orderType = jsonObject.getInteger("orderType");
                iRedisService.setAdd(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY_PREFIX + orderType, mobile);
            case DISTRIBUTE_ORDER: // 2 派发订单处理: 派发订单至[可抢单用户] 的[可抢订单队列]中，通过定时任务拽去订单，并完成推送
                RushOrderInfo entity = JSONObject.parseObject(messageDomain.getContent(), RushOrderInfo.class);
                r = doPushOrderHandle(entity);
                break;

            case PUSH_ORDER_TO_SPECIAL_USER:
                // DISTRIBUTE_ORDER中已处理: 派单后直接下发订单至用户的抢单队列中
                break;
            case PRINT_SERVER_TIME: // 测试使用，打印系统时间
                r = iNettyService.sendMessage(messageDomain, false);
                break;
            default:
                log.warn("UnSupport Topic[ {} ].", topic);
                return;
        }
        log.info("Redis 队列消费[{}]完成，{}.", actionTypeEnum.getDescribe(), r);
    }

    @Autowired
    AccountService accountService;
    @Autowired
    IUserService iUserService;

    /**
     * 派发订单处理
     * 用户校验规则 ： 1、用户账户是正常用户，且激活。2、账户可用余额大于1000，并且，可用余额-1000要大于本订单金额。
     *
     * @param entity
     */
    private R doPushOrderHandle(RushOrderInfo entity) {
        // 1 取出指定类型的可抢单用户
        Set<String> users = iRedisService.setMembers(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY_PREFIX + entity.getOrderType());

        if (CollectionUtils.isEmpty(users)) {
            return R.ok("可抢购用户队列为空，无需派发订单!");
        }
        int validUserCount = 0;

        List<String> validUsers = users.stream().map(u -> {
            Map<String, Object> rMap = iUserService.getAccountBaseInfo(null, u);
            BigDecimal canuseAmount;
            if (!CollectionUtils.isEmpty(rMap) && 1 == (Integer) rMap.get("RECV_STATUS") && null != (canuseAmount = (BigDecimal) rMap.get("CANUSE_AMOUNT"))) {
                if (canuseAmount.compareTo(OrdersService.MIN_ACCOUNT_BALANCE_CAN_RECV) > 0 && canuseAmount.subtract(entity.getSendAmount()).compareTo(OrdersService.MIN_ACCOUNT_BALANCE_CAN_RECV) > 0) {
                    return u;
                }
            }
            return null;
        }).filter(u -> null != u).collect(Collectors.toList());

        // 3 订单派发
        if (!CollectionUtils.isEmpty(validUsers)) {
            validUserCount = validUsers.size();
            validUsers.stream().forEach(v -> {
                // order_list_can_buy:186:1 [list]
                // users_can_rush_buy:1 [set]
                iRedisService.leftPush(RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + v + ":" + entity.getOrderType(), JSONObject.toJSONString(entity));

            });
        }
        log.info("下发订单[{}]至[{}]个用户:{}", entity.getOrderSn(), validUsers.size(), validUsers);
        return R.ok("已下发订单[ " + entity.getOrderSn() + " ]至[ " + validUserCount + " ]个用户.");
    }
}
