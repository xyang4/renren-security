package io.renren.modules.task.schedual;

import io.renren.common.config.RenrenProperties;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.task.BaseHandleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class UserRelevantHandleTask extends BaseHandleTask {


    /**
     * 清理存活用户，防止用户异常下线造成OOM
     */
    @Scheduled(cron = "0 */${renren.web-socket.expire} * * * ?")
    public void clearActiveUser() {
        iNettyService.clearActiveUser();
    }

}
