package io.renren.modules.task.schedual;

import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    @Scheduled(fixedRate = 5 * 1000)
    public void pushOrder() {
        log.info("Task[{}] Begin...", WebSocketActionTypeEnum.PULL_ORDER.getDescribe());
        // 1 query active user
        Set<String> keys = redisTemplate.keys("online:*");
        if (CollectionUtils.isEmpty(keys)) return;
        // 2 push msg to special user by mobile
        // TODO
    }
}
