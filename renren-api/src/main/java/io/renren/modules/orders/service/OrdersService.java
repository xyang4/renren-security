package io.renren.modules.orders.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.R;
import io.renren.modules.netty.domain.WebSocketResponseDomain;
import io.renren.modules.orders.domain.OrderRule;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.form.OrderPageForm;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
public interface OrdersService extends IService<OrdersEntity> {


    /**
     * 开始接单
     *
     * @param mobile    用户手机号
     * @param orderRule 订单规则
     * @param size      接收订单条数
     * @return
     */
    List<Map<String, Object>> receiveValidOrder(String mobile, OrderRule orderRule, int size);

    /**
     * 抢购
     *
     * @param recvUserId    接单用户编号
     * @param mobile    接单用户手机号
     * @param orderType 订单类型
     * @param orderId   订单编号
     * @return map
     */
    WebSocketResponseDomain rushToBuy(Integer recvUserId, String mobile,String orderType, String orderId);

    /**
     * 订单状态通知
     *
     * @param noticeType
     * @param orderType
     * @param orderId
     */
    void orderStatusNotice(int noticeType, String orderType, String orderId);

    /**
     * 列表查询
     *
     * @param page
     * @param orderStatus
     * @return
     */
    List<Map<String, Object>> listOrder(Page<Map<String, Object>> page, OrdersEntityEnum.OrderType orderType, byte orderStatus);

    /**
     * 创建订单
     *
     * @param orderSources 订单来源 充值|提现
     * @param mobile       用户手机号
     * @param amount       交易金额
     * @return 如果成功返回订单信息，失败返回异常
     */
    Map<String, Object> createOrder(OrdersEntityEnum.OrderSources orderSources, String mobile, double amount);

    /**
     * 申请预处理
     */
    Map applyOrder(Integer merId, String orderDate, int orderType, String orderSn, String payType, String sendAmount, String notifyUrl);


    /**
     * 订单有效性校验： {@link OrdersEntity } {@link io.renren.modules.orders.domain.RushOrderInfo}
     *
     * @param orderInfo
     * @return
     */
    Object checkValidity(Object orderInfo);

    boolean addOrder(OrdersEntity ordersEntity);

    List<OrdersEntity> getOrders(Map<String, Object> param);

    /**
     * 搬运工提现
     *
     * @param userId
     * @param amount
     * @param accountName
     * @param accountNo
     * @return
     */
    R hamalWithdraw(Integer userId, String amount, String accountName, String accountNo,String bankName);

    /**
     * 搬运工充值
     *
     * @param userId
     * @param amount
     * @param accountName
     * @param accountNo
     * @return
     */
    R hamalRecharge(Integer userId, String amount, String accountName, String accountNo);

    //查询用户接单或者发单列表
    List<OrdersEntity> getSendOrRecvOrderList(OrderPageForm orderPageForm);

    /**
     * 订单推送
     *
     * @param async     是否同步
     * @param orderType 订单类型
     */
    void pushSpecialOrder(boolean async, OrdersEntityEnum.OrderType orderType);

    /**
     * 订单推送
     *
     * @param orderType
     */
    void pushSpecialOrder(OrdersEntityEnum.OrderType orderType);

    /**
     * 异步推送指定类型的订单
     *
     * @param merRecharge
     */
    @Async
    void asyncPushSpecialOrder(OrdersEntityEnum.OrderType merRecharge);

    /**
     * 异步批量推送订单至指定用户
     *
     * @param mobile
     * @param orderType
     */
    @Async
    void asyncBatchPushOrderToUser(String mobile, int orderType);


    /**
     * 抢单成功:更新订单为接单成功状态
     * recvUserId:接单人用户id
     */
    Map reciveOrderSuccess(Integer recvUserId, String orderType,String orderId);

    /**
     * 确认收款
     */
    R sureRecvOrder(Integer orderId, BigDecimal confirmAmount);

}

