package io.renren.modules.task.schedual;

import io.renren.common.config.RenrenProperties;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.service.OrdersService;
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
public class UserOrderHandleTask {

    @Autowired
    IRedisService iRedisService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    INettyService iNettyService;
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    OrdersService ordersService;

    /**
     * 给指定在线用户推送可抢订单
     */
    @Scheduled(fixedDelay = 5 * 1000)
    public void pushOrder() {
        for (OrdersEntityEnum.OrderType item : OrdersEntityEnum.OrderType.values()) {
            ordersService.pushSpecialOrder(false, item);
        }
    }

    /**
     * 清理存活用户，防止用户异常下线造成OOM
     */
    @Scheduled(cron = "0 */${renren.web-socket.expire} * * * ?")
    public void clearActiveUser() {
        iNettyService.clearActiveUser();
    }


    //当前订单状态
    //订单状态 0初始1-订单提交 通知抢单,待接单2-已接单3-用户取消
    // 4-超时未接单系统取消5-等待打款并确认6-超时未打款取消7-支付受限,重新派单
    // 8-发单确认打款9-收单确认已打款 ,订单完成
    //15-等待打款--更换付款方式30-客服处理为取消31-客服处理为完成

    // TODO 订单超时处理 ，目前处理两个ORDER_TYPE类型的 1搬运工充值3商户充值
    //按照：TIMEOUT_RECV接单超时、TIMEOUT_PAY支付超时、和当前ORDER_STATE状态进行清理
    //0、如果订单类型不是4、6、9、30、31，则执行以下：
    //1、 如果当前时间超过“接单超时”时间,且订单状态是0、或1，则认为订单失败，更新订单为4。

    //2、 如果当前时间超过“支付超时”时间,且订单状态是2、或5，则认为订单失败，更新订单为6；
    // 本订单要下发给当前接单者，告知该接单者本订单已经支付超时，订单处理为了6。

    //3、清理掉队列中的相关订单。

}
