package io.renren.common.listener;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.utils.R;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.entity.OrdersEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
    private static int no = 0;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        String m = message.toString();
        log.info("Redis 消息队列消费开始：Message[{}].", m);
        if (!m.startsWith("{") || !m.endsWith("}")) {
            log.warn("消息格式错误");
            return;
        }

        RedisMessageDomain messageDomain = JSONObject.parseObject(m, RedisMessageDomain.class);
        if (null == messageDomain || null == messageDomain.getTopic() || null == messageDomain.getContent()) {
            log.warn("Redis 消息队列消费完成!! {}", RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR.getMsg());
            return;
        }

        R r = null;
        WebSocketActionTypeEnum actionTypeEnum = WebSocketActionTypeEnum.getByCode(topic);
        switch (actionTypeEnum) {
            case BEGIN_RECEIPT: // 追加用户至可抢订单的用户集合中
                String mobile = messageDomain.getContent();
                iRedisService.setAdd(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY, mobile);
            case DISTRIBUTE_ORDER: // 派发订单处理
                OrdersEntity entity = JSONObject.parseObject(messageDomain.getContent(), OrdersEntity.class);
                r = doDisteributeOrderHandle(entity);
                break;
            case PUSH_ORDER_TO_SPECIAL_USER: // 追加订单至指定用户的可抢订单队列中
                OrdersEntity t = JSONObject.parseObject(messageDomain.getContent(), OrdersEntity.class);
                iRedisService.leftPush(RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + t.getRecvUserId(), t.getOrderSn());
                break;
            case PRINT_SERVER_TIME:
                r = iNettyService.sendMessage(messageDomain, false);
                break;
            default:
                log.warn("UnSupport Topic[ {} ].", topic);
                return;
        }
        log.info("Consumer Execution Done：{}.", r);
    }

    @Autowired
    AccountService accountService;

    /**
     * 派发订单处理
     * 用户校验规则 ： 1、用户账户是正常用户，且激活。2、账户可用余额大于1000，并且，可用余额-1000要大于本订单金额。
     *
     * @param entity
     */
    private R doDisteributeOrderHandle(OrdersEntity entity) {
        // 1 取出可抢单用户
        Set<String> users = iRedisService.setMembers(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY);

        if (CollectionUtils.isEmpty(users)) {
            return R.ok("可抢购用户队列为空，无需派发订单!");
        }
//        TODO
        int validUserCount = 0;
        //2 用户校验
        List<Integer> validUsers = users.stream().map(u -> {
            AccountEntity accountEntity = accountService.getById(u);
            if (null == accountEntity) {
                return null;
            } else {
                return accountEntity.getUserId();
            }
        }).filter(v -> null != v).collect(Collectors.toList());

        // 3 派发
        if (!CollectionUtils.isEmpty(validUsers)) {
            validUserCount = validUsers.size();
            validUsers.forEach(v -> {
                OrdersEntity o = entity;
                o.setRecvUserId(v);
                iRedisService.sendMessageToQueue(new RedisMessageDomain(WebSocketActionTypeEnum.PUSH_ORDER_TO_SPECIAL_USER, System.currentTimeMillis(), o));
            });
        }
        return R.ok("已下发订单[" + entity.getOrderSn() + "]至[" + validUserCount + "]个用户.");
    }
}
