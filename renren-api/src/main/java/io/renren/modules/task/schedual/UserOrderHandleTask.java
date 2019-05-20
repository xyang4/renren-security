package io.renren.modules.task.schedual;

import io.renren.common.config.RenrenProperties;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 用户定点处理定时任务<br>
 * 1 给活跃用户周期地推送可抢订单
 * 2 考虑订单周期，定期地清理无用的订单
 * 3 redis 其他数据清理
 * .
 * .
 */
@Slf4j
@Component
public class UserOrderHandleTask {

    @Autowired
    IRedisService iRedisService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    INettyService iNettyService;
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    OrdersService ordersService;

    @Scheduled(fixedRate = 5 * 1000)
    public void pushOrder() {
        // 1 query active user
        List<String> onlineUserWithMobile = WebSocketServerHandler.ONLINE_USER_WITH_MOBILE;
        Set<String> userSetCanRushBuy = iRedisService.setMembers(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY);

        log.info("Exec Task[{}]:users_Online[{}] users_canRushBuy[{}] ...", WebSocketActionTypeEnum.PULL_ORDER.getDescribe(), onlineUserWithMobile.size(), userSetCanRushBuy.size());
        // 同 handleWebSocketRequest.handleWebSocketRequest: ACTIVE & BEGIN_RECEIPT 处理
        if (CollectionUtils.isEmpty(onlineUserWithMobile) || CollectionUtils.isEmpty(userSetCanRushBuy)) {
            return;
        }
        // todo 批处理优化
        onlineUserWithMobile.stream()
                .filter(v -> userSetCanRushBuy.contains(v)).forEach(v -> {
            long orders = iRedisService.listSize(RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + v);
            orders = orders > renrenProperties.getBatchPushOrderNumMax() ? renrenProperties.getBatchPushOrderNumMax() : orders;

            // 2 push msg to special user by mobile
            if (orders > 0) {
                for (int i = 0; i < orders; i++) {
                    String orderInfo = iRedisService.pull(RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + v);
                    if (ordersService.checkValidity(orderInfo)) {
                        iNettyService.asyncSendMessage(v, orderInfo);
                    }
                }
            }
        });
    }

}
