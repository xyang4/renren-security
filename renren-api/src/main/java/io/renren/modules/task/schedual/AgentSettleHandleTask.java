package io.renren.modules.task.schedual;

import io.renren.modules.task.BaseHandleTask;
import io.renren.modules.user.service.AgentSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 代理商结算
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "renren", name = "task-open", havingValue = "true")
public class AgentSettleHandleTask extends BaseHandleTask {

    @Autowired
    AgentSettleService agentSettleService;

    /**
     * 代理商每日结算
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void agentSettle() {
        log.info("代理商每日结算开始。。。");
        agentSettleService.agentSettle();
        log.info("代理商每日结算结束。。。");
    }

}
