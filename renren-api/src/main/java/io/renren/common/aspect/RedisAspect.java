package io.renren.common.aspect;

import io.renren.common.config.RenrenProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RedisAspect {

    @Autowired
    RenrenProperties renrenProperties;

    @Around("this(io.renren.modules.common.service.IRedisService)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (renrenProperties.isOpenRedisLogger()) {
//            TODO Redis 操作日志记录
            log.info("Execution Redis handle!!! Method[{}] Args {}.", point.getSignature().getName(), Arrays.asList(point.getArgs()));

        }
        return point.proceed();
    }
}