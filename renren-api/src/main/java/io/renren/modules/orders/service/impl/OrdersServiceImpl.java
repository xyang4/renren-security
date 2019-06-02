package io.renren.modules.orders.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.config.RenrenProperties;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.exception.RRException;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.domain.WebSocketResponseDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.dao.OrdersDao;
import io.renren.modules.orders.domain.OrderRule;
import io.renren.modules.orders.domain.RushOrderInfo;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.form.OrderPageForm;
import io.renren.modules.orders.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {
    @Autowired
    IRedisService iRedisService;

    @Autowired
    OrdersDao ordersDao;
    @Autowired
    AccountService accountService;
    @Autowired
    RenrenProperties renrenProperties;

    @Override
    public List<Map<String, Object>> receiveValidOrder(String mobile, OrderRule orderRule, int size) {
        //        TODO
        return null;
    }

    @Override
    @Transactional
    public WebSocketResponseDomain rushToBuy(String mobile, String orderType, String orderId) {
        WebSocketResponseDomain r = new WebSocketResponseDomain(WebSocketActionTypeEnum.RUSH_ORDERS.getCommand(), null);
        if (null != checkValidity(orderId)) {
            r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_INVALID_ORDER.getCode());
            r.setMsg(WebSocketResponseDomain.ResponseCode.ERROR_INVALID_ORDER.getMsg());
            return r;
        }
        String lockVal = iRedisService.getVal(RedisCacheKeyConstant.LOCK_ORDER_PREFIX + orderId);
        if (StringUtils.isBlank(lockVal)) {
            String lockKey = RedisCacheKeyConstant.LOCK_ORDER_PREFIX + orderId;
            iRedisService.set(lockKey, mobile, renrenProperties.getOrderRushLockSecond(), TimeUnit.SECONDS);

            Map rMap = reciveOrderSuccess(mobile, orderType, orderId);
            if (null == rMap) {
                r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_HANDLE.getCode());
                r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_HANDLE.getCode());
            } else {
                // 操作成功，通知所有用户该单已被抢
                orderStatusNotice(1, orderType, orderId);
                r.setData(rMap);
            }

            iRedisService.delKey(lockKey);
        } else {
            r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_RUSH_BEING_QUEUE.getCode());
            r.setMsg(WebSocketResponseDomain.ResponseCode.ERROR_RUSH_BEING_QUEUE.getMsg());
        }
        return r;
    }

    @Override
    public void orderStatusNotice(int noticeType, String orderType, String orderId) {
        switch (noticeType) {
            case 1: // 通知所有已下发抢单信息的用户，改单已被抢
                // 已下发用户查询
                String cacheKey = RedisCacheKeyConstant.USERS_PUSHED_RUSH_ORDER_PREFIX + orderType + StaticConstant.SPLIT_CHAR_COLON + orderId;
                Set<String> pushedUsers = iRedisService.setMembers(cacheKey);
                for (String item : pushedUsers) {
                    WebSocketResponseDomain responseDomain = new WebSocketResponseDomain(WebSocketActionTypeEnum.CANCEL_PUSHED_ORDER.getCommand(), orderId);
                    iRedisService.removeSetMember(cacheKey, item);
                    iNettyService.asyncSendMessage(item, responseDomain);
                }
                break;
            default:
        }
    }

    @Override
    public List<Map<String, Object>> listOrder(Page<Map<String, Object>> page, OrdersEntityEnum.OrderType orderType, byte orderStatus) {
        //        TODO
        return null;
    }

    @Override
    public Map<String, Object> createOrder(OrdersEntityEnum.OrderSources orderSources, String mobile, double amount) {
        //        TODO
        return null;
    }

    /**
     * 订单申请，创建预订单
     */
    public Map applyOrder(Integer merId, String orderDate, int orderType,
                          String orderSn, String payType, String sendAmount, String notifyUrl) {
        Map returnMap = new HashMap();
        //外围接口做必要的数据校验后调用本方法
        //创建新订单
        OrdersEntity orders = new OrdersEntity();
        orders.setSendUserId(merId);//发送用户
        orders.setOrderDate(orderDate);//订单日期
        orders.setOrderType(orderType);
        orders.setOrderSn(orderSn);//商户原始订单
        orders.setPayType(payType);//付款类型
        orders.setOrderState(OrdersEntityEnum.OrderState.b.getValue());//订单状态
        orders.setSendAmount(new BigDecimal(sendAmount));//发送金额
        orders.setAmount(new BigDecimal(sendAmount));//金额
        orders.setIsApi(1);
        orders.setPlatDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        orders.setNotifyUrl(notifyUrl);//回调地址
        orders.setTimeoutRecv(3 * 60);//抢单超时时间，秒 TODO
        orders.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        boolean boo = this.save(orders);
        //验证
        if (boo == true && (orders.getOrderId() != null && orders.getOrderId() > 0)) {//检查订单是否创建成功
            RushOrderInfo rushOrderInfo = new RushOrderInfo(
                    orders.getOrderId(),
                    orders.getOrderSn(),
                    orders.getCreateTime(),
                    orders.getOrderType(),
                    orders.getTimeoutRecv());
            RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.DISTRIBUTE_ORDER, System.currentTimeMillis(), rushOrderInfo);
            iRedisService.sendMessageToQueue(messageDomain);

            Integer orderId = orders.getOrderId();
            returnMap.put("orders", orders);//返回订单
            returnMap.put("code", 0);
            return returnMap;//添加成功
        } else {
            returnMap.put("code", -1);
            return returnMap;//添加失败
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public R hamalWithdraw(Integer userId, String amount, String accountName, String accountNo) {
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(amount));
        ordersEntity.setSendAmount(new BigDecimal(amount));
        ordersEntity.setSendUserId(userId);
        ordersEntity.setSendAccountName(accountName);
        ordersEntity.setSendAccountNo(accountNo);
        ordersEntity.setOrderType(OrdersEntityEnum.OrderType.PORTER_WITHDROW.getValue());
        ordersEntity.setOrderState(OrdersEntityEnum.OrderState.INIT.getValue());
        ordersEntity.setOrderDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        ordersEntity.setPayType(OrdersEntityEnum.PayType.BANK.getValue());
        ordersEntity.setIsApi(0);
        ordersEntity.setPlatDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if (r > 0) {
            //更改账户可用余额和冻结金额
            int r1 = accountService.updateAmount(userId, new BigDecimal(amount).negate(), new BigDecimal(amount));
            if (r1 <= 0) {
                throw new RRException("更改账户金额异常");
            }
        }
        return R.error();
    }

    @Override
    public R hamalRecharge(Integer userId, String amount, String accountName, String accountNo) {
        //查询用户是否有进行中的充值
        Map<String, Object> param = new HashMap<>();
        param.put("sendUserId", userId);
        param.put("orderType", OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        List<Integer> orderStates = new ArrayList<>();
        orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
        orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        param.put("includeState", orderStates);
        List<OrdersEntity> orders = ordersDao.getOrders(param);
        if (orders != null && orders.size() > 0) {
            return R.error(-1, "有进行中的订单，稍后重试");
        }
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(amount));
        ordersEntity.setSendAmount(new BigDecimal(amount));
        ordersEntity.setSendUserId(userId);
        ordersEntity.setSendAccountName(accountName);
        ordersEntity.setSendAccountNo(accountNo);
        ordersEntity.setOrderType(OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        ordersEntity.setOrderState(OrdersEntityEnum.OrderState.b.getValue());
        ordersEntity.setOrderDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        ordersEntity.setPayType(OrdersEntityEnum.PayType.BANK.getValue());
        //todo 订单超时时间
        ordersEntity.setTimeoutRecv(60);
        ordersEntity.setTimeoutDown(120);
        ordersEntity.setIsApi(0);
        ordersEntity.setPlatDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if (r > 0) {
            //todo websocket 推送
        }
        return R.ok(ordersEntity);
    }

    @Override
    public boolean addOrder(OrdersEntity ordersEntity) {
        ordersEntity.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        return this.save(ordersEntity);
    }


    @Override
    public List<OrdersEntity> getOrders(Map<String, Object> param) {
        return ordersDao.getOrders(param);
    }

    @Override
    public List<OrdersEntity> getSendOrRecvOrderList(OrderPageForm orderPageForm) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", orderPageForm.getUserId());
        param.put("orderType", orderPageForm.getOrderType());
        param.put("orderState", orderPageForm.getOrderState());
        //默认第一页5条
        Integer pageIndex = orderPageForm.getPageIndex() == null ? 1 : orderPageForm.getPageIndex();
        Integer pageSize = orderPageForm.getPageSize() == null ? 5 : orderPageForm.getPageSize();
        Page<OrdersEntity> ordersPage = new Page<>(pageIndex, pageSize);
        ordersPage.setRecords(ordersDao.getSendOrRecvOrderList(param, ordersPage));
        return ordersPage.getRecords();
    }

    @Autowired
    INettyService iNettyService;

    @Async
    public void asyncBatchPushOrderToUser(String mobile, int orderType) {
        // 1 订单拉取
        String redisKey = RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + mobile + ":" + orderType;
        long ordersNum = iRedisService.listSize(redisKey);
        log.info("可消费订单数量[{}] MaxAllowNum[{}]", ordersNum, renrenProperties.getBatchPushOrderNumMax());
        if (ordersNum < 1) {
            return;
        }
        ordersNum = ordersNum > renrenProperties.getBatchPushOrderNumMax() ? renrenProperties.getBatchPushOrderNumMax() : ordersNum;

        // 2 订单遍历推送下发
        for (int i = 0; i < ordersNum; i++) {
            String orderInfo = iRedisService.pull(redisKey);
            WebSocketResponseDomain responseDomain;
            if (null != (responseDomain = (WebSocketResponseDomain) checkValidity(orderInfo))) {
                // 已下发的抢单用户信息
                responseDomain.setA(WebSocketActionTypeEnum.RUSH_ORDERS.getCommand());
                RushOrderInfo rushOrderInfo = (RushOrderInfo) responseDomain.getData();
                iRedisService.setAdd(RedisCacheKeyConstant.USERS_PUSHED_RUSH_ORDER_PREFIX + orderType + StaticConstant.SPLIT_CHAR_COLON + rushOrderInfo.getOrderId(), mobile);
                iNettyService.asyncSendMessage(mobile, responseDomain);
            }
        }
    }

    @Override
    public void pushSpecialOrder(boolean async, OrdersEntityEnum.OrderType merRecharge) {
        if (async) {
            OrdersService ordersService = SpringContextUtils.getBean(OrdersService.class);
            ordersService.asyncPushSpecialOrder(merRecharge);
        } else {
            pushSpecialOrder(merRecharge);
        }
    }

    @Override
    @Async
    public void asyncPushSpecialOrder(OrdersEntityEnum.OrderType merRecharge) {
        pushSpecialOrder(merRecharge);
    }

    @Override
    public void pushSpecialOrder(OrdersEntityEnum.OrderType orderType) {

        // 1 查询并筛选有效的在线用户
        List<String> onlineUserWithMobile = WebSocketServerHandler.ONLINE_USER_WITH_MOBILE;
        Set<String> userSetCanRushBuy = iRedisService.setMembers(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY_PREFIX + orderType.getValue());

        log.info("Task[{}] Exec Begin:OrderType[{}] Users_Online[{}] Users_CanRushBuy[{}] ...", orderType.getName(), WebSocketActionTypeEnum.PULL_ORDER.getDescribe(), onlineUserWithMobile.size(), userSetCanRushBuy.size());
        // 同 handleWebSocketRequest.handleWebSocketRequest: ACTIVE & BEGIN_RECEIPT 处理
        if (CollectionUtils.isEmpty(onlineUserWithMobile) || CollectionUtils.isEmpty(userSetCanRushBuy)) {
            return;
        }

        //2、给有效用户推送指定类型的订单
        onlineUserWithMobile.stream().filter(v -> userSetCanRushBuy.contains(v)).forEach(v -> {
            asyncBatchPushOrderToUser(v, orderType.getValue());
        });
    }

    @Override
    public Object checkValidity(Object orderInfo) {
        Object r = null;
        if (orderInfo instanceof OrdersEntity) {
            OrdersEntity entity = (OrdersEntity) orderInfo;
            log.info("checkValidity:", entity);
            // TODO 订单有效性校验：超时时间等
        } else if (orderInfo instanceof String) {
            // 同 RedisMessageReceiver.onMessage : PUSH_ORDER_TO_SPECIAL_USER 处理
            if (((String) orderInfo).contains("{")) {
                RushOrderInfo rushOrderInfo = JSONObject.parseObject((String) orderInfo, RushOrderInfo.class);
                Date expireDate = DateUtils.addDateSeconds(DateUtils.stringToDate(rushOrderInfo.getCreateTime(), DateUtils.DATE_TIME_PATTERN), rushOrderInfo.getTimeoutRecv());
                if (new Date().compareTo(expireDate) < 0) {
                    r = new WebSocketResponseDomain(null, rushOrderInfo);
                }
            } else {//仅对orderId校验
                // TODO 订单有效性校验：超时时间等
            }

        }
        return r;
    }

//    /**
//     * 开始接单: 接收用户，接单类订单
//     */
//    public Map startGetOrder(String recvUserId,String types){
//        Map returnMap = new HashMap();
//        //设置用户接单状态，及接单类型
//
//
//        return returnMap;
//    }
//
//    /**
//     * 停止接单
//     */
//    public Map stopGetOrder(){
//        Map returnMap = new HashMap();
//        //设置用户接单状态
//
//
//        return returnMap;
//    }


    /**
     * 查询用户订单：已抢,完成
     */
    public Map selectOrder(String recvUserId, String orderType, String orderState) {
        Map returnMap = new HashMap();

        //查询订单状态及信息


        //ridis事物提交


        return returnMap;
    }

    /**
     * 确认收款
     */
    public Map recvAmount(BigDecimal recvAmount, String orderType, String orderState) {
        Map returnMap = new HashMap();

        //查询订单状态及信息


        //ridis事物提交


        return returnMap;
    }

    /**
     * 订单超时,系统取消
     */
    public Map orderTimeout(String recvUserId, String orderType, String orderState) {
        Map returnMap = new HashMap();

        //{"a":45,"code":0,"order_id":2528445,"is_api":"1",
        // "order_sn":"414028","order_type":"1","msg":"\u8d85\u65f6\u672a\u4ed8\u6b3e\u7cfb\u7edf\u53d6\u6d88!"}

        return returnMap;
    }

    /**
     * 订单查询提醒：用户已付款，下发提醒
     */
    public Map orderRemind(String recvUserId, String orderType, String orderState) {
        Map returnMap = new HashMap();

        //{"a":64,"code":0,"order_id":2654348}


        return returnMap;
    }

    /**
     * 抢单成功:更新订单为接单成功状态
     */
    public Map reciveOrderSuccess(String orderId, String orderType, String recvMobile) {
        //TODO
        Map returnMap = new HashMap();
        returnMap.put("orderId", orderId);
        returnMap.put("orderType", orderType);

        return returnMap;
    }

    //下发余额


}
