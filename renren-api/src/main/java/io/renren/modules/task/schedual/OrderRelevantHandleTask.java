package io.renren.modules.task.schedual;

import io.renren.common.enums.OrdersEntityEnum;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.task.BaseHandleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderRelevantHandleTask extends BaseHandleTask {

    @Autowired
    OrdersService ordersService;

    /**
     * 推送可抢订单至在线可抢单用户
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void pushOrder() {
        for (OrdersEntityEnum.OrderType item : OrdersEntityEnum.OrderType.values()) {
            ordersService.pushSpecialOrder(false, item);
        }
    }

    /*
        当前订单状态


         TODO 订单超时处理 ，目前处理两个ORDER_TYPE类型的 1搬运工充值3商户充值
        按照：TIMEOUT_RECV接单超时、TIMEOUT_PAY支付超时、和当前ORDER_STATE状态进行清理
        0、如果订单类型不是4、6、9、30、31，则执行以下：
        1、 如果当前时间超过“接单超时”时间,且订单状态是0、或1，则认为订单失败，更新订单为4。

        2、 如果当前时间超过“支付超时”时间,且订单状态是2、或5，则认为订单失败，更新订单为6；
         本订单要下发给当前接单者，告知该接单者本订单已经支付超时，订单处理为了6。

        3、清理掉队列中的相关订单。
    */
    @Scheduled(fixedRate = 2 * 1000)
    public void downOrderHandle() {
        ordersService.execOrderTimeOutHandle(1);
    }

    /**
     * 接单超时处理，状态：0、或1 ，若已超时修改为 4
     */
    @Scheduled(fixedRate = 2 * 1000)
    public void recvOrderHandle() {
        ordersService.execOrderTimeOutHandle(2);
    }


    /**
     * 支付超时处理，状态：2、5、6， 若已超时修改为 4
     */
    @Scheduled(fixedRate = 2 * 1000)
    public void payOrderHandle() {
        ordersService.execOrderTimeOutHandle(3);
    }

}
