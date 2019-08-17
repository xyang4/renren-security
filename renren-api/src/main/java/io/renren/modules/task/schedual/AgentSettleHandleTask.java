package io.renren.modules.task.schedual;

import io.renren.common.utils.DateUtils;
import io.renren.modules.agent.service.AgentSettleUserRecordService;
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
    @Autowired
    AgentSettleUserRecordService agentSettleUserRecordService;

    /**
     * 代理商每日结算
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void agentSettle() {
        log.info("代理商每日结算开始。。。");
        agentSettleService.agentSettle();
        log.info("代理商每日结算结束。。。");
    }

    /**
     * 用户接单统计跑批任务
     */
//    @Scheduled(cron = "${renren.task.recv-user-report}")
    @Scheduled(cron = "0 0 2 /1 * ?")
    public void execUserRecvReport() {
        String settleDate = DateUtils.getDate(null, -1);
        log.info("用户接单统计开始，settleDate[{}]", settleDate);
        int i = agentSettleUserRecordService.execUserRecvReport(settleDate);
        log.info("用户接单统计结束，settleDate[{}] R[{}].", settleDate, i);
    }

    /**
     * 代理收益结算跑批任务
     */
//    @Scheduled(cron = "${renren.task.agent-profit-settle}")
    @Scheduled(cron = "0 0 2 /1 * ?")
    public void execAgentProfitSettle() {
        String settleDate = DateUtils.getDate(null, -1);
        log.info("用户接单统计开始，settleDate[{}]", settleDate);
        int i = agentSettleUserRecordService.execUserRecvReport(settleDate);
        log.info("用户接单统计结束，settleDate[{}] R[{}].", settleDate, i);
    }

}
