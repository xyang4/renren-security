package io.renren.modules.task.schedual;

import lombok.extern.slf4j.Slf4j;
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
public class TestTask {


    @Scheduled(fixedRate = 5 * 1000)
    public void test() {
// TODO
    }

}
