package io.renren.modules.task.schedual;

import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.task.BaseHandleTask;
import io.renren.modules.user.service.AgentSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 发起假订单程序
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "renren", name = "task-open", havingValue = "true")
public class ShamOrdersHandleTask extends BaseHandleTask {

    @Autowired
    OrdersService ordersService;
    /**
     * 发起假订单程序
     */
    @Scheduled(fixedRate = 3 * 1000)
    public void shamOrders() {
        log.info("发起假订单程序开始。。。");
        ordersService.shamOrders();
        log.info("发起假订单程序结束。。。");
    }

}
