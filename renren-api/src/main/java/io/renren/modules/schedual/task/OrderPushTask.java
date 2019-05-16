package io.renren.modules.schedual.task;

import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Slf4j
@Component
public class OrderPushTask {

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
