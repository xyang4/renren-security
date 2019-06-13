package io.renren.modules.task;

import io.renren.common.config.RenrenProperties;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.service.INettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class BaseHandleTask {
    @Autowired
    protected IRedisService iRedisService;
    @Autowired
    protected RedisTemplate<String, String> redisTemplate;
    @Autowired
    protected INettyService iNettyService;
    @Autowired
    protected RenrenProperties renrenProperties;
}
