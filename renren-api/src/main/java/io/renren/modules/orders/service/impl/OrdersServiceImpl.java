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
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.entity.PayChannelDetail;
import io.renren.modules.account.service.AccountLogService;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.account.service.PayChannelService;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.domain.WebSocketResponseDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.dao.OrdersDao;
import io.renren.modules.orders.domain.BaseOrderInfo;
import io.renren.modules.orders.domain.OrderRule;
import io.renren.modules.orders.domain.RushOrderInfo;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.entity.OrdersLogEntity;
import io.renren.modules.orders.form.OrderPageForm;
import io.renren.modules.orders.service.OrdersLogService;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.system.service.IConfigService;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.execute.Execute;
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
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {
    @Autowired
    IRedisService iRedisService;
    @Autowired
    private IUserService userService;
    @Autowired
    OrdersDao ordersDao;
    @Autowired
    AccountService accountService;
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    private PayChannelService payChannelService;
    @Autowired
    private OrdersLogService ordersLogService;
    @Autowired
    private IConfigService configService;

    @Override
    public List<Map<String, Object>> receiveValidOrder(String mobile, OrderRule orderRule, int size) {
        //        TODO
        return null;
    }

    @Override
    @Transactional
    public WebSocketResponseDomain rushToBuy(Integer recvUserId, String mobile, String orderType, String orderId) {
        WebSocketResponseDomain r = new WebSocketResponseDomain(WebSocketActionTypeEnum.RUSH_ORDERS_RESULT.getCommand(), null);
        if (null != checkValidity(orderId)) {
            r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_INVALID_ORDER.getCode());
            r.setMsg(WebSocketResponseDomain.ResponseCode.ERROR_INVALID_ORDER.getMsg());
            return r;
        }
        String lockVal = iRedisService.getVal(RedisCacheKeyConstant.LOCK_ORDER_PREFIX + orderId);
        if (StringUtils.isBlank(lockVal)) {
            String lockKey = RedisCacheKeyConstant.LOCK_ORDER_PREFIX + orderId;
            iRedisService.set(lockKey, mobile, renrenProperties.getOrderRushLockSecond(), TimeUnit.SECONDS);

            Map rMap = reciveOrderSuccess(recvUserId, orderType, orderId);
            if (null == rMap) {
                r.setCode(WebSocketResponseDomain.ResponseCode.ERROR_HANDLE.getCode());
                r.setMsg(WebSocketResponseDomain.ResponseCode.ERROR_HANDLE.getMsg());
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
        orders.setTimeoutDown(60 * 60);//订单总超时 TODO 待定

        //订单超时时间
        String timeOutRecv = configService.selectConfigByKey("mer_charge_timeout_recv");
        String timeOutPay = configService.selectConfigByKey("mer_charge_timeout_pay");
        orders.setTimeoutRecv(timeOutRecv == null ? 30 : Integer.parseInt(timeOutRecv));
        orders.setTimeoutPay(timeOutPay == null ? 600 : Integer.parseInt(timeOutPay));
        //搬运工充值发单费率、搬运工充值接单费率
        String sendRate = configService.selectConfigByKey("send_mer_chargeRate");
        String recvRate = configService.selectConfigByKey("recv_mer_chargeRate");
        orders.setSendRate(sendRate == null ? new BigDecimal(0) : new BigDecimal(sendRate));
        orders.setRecvRate(recvRate == null ? new BigDecimal(0) : new BigDecimal(recvRate));
        BigDecimal sa = orders.getSendAmount().multiply(orders.getSendRate()).setScale(2, BigDecimal.ROUND_DOWN);
        orders.setSendRateAmount(sa);
        BigDecimal ra = orders.getSendAmount().multiply(orders.getRecvRate()).setScale(2, BigDecimal.ROUND_DOWN);
        orders.setRecvRateAmount(ra);

        orders.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        boolean boo = this.save(orders);
        //验证
        if (boo == true && (orders.getOrderId() != null && orders.getOrderId() > 0)) {//检查订单是否创建成功
            RushOrderInfo rushOrderInfo = new RushOrderInfo(
                    orders.getOrderId(),
                    orders.getOrderSn(),
                    orders.getCreateTime(),
                    orders.getOrderType(),
                    orders.getTimeoutRecv(),
                    orders.getPayType(), orders.getSendAmount());
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
    public R hamalWithdraw(Integer userId, String amount, String accountName, String accountNo, String bankName) {
        //查询搬运工提现相关配置
        String minAmountStr = configService.selectConfigByKey("send_porter_withMin");
        BigDecimal minAmount = minAmountStr == null ? new BigDecimal(100) : new BigDecimal(minAmountStr);
        if (new BigDecimal(amount).compareTo(minAmount) < 0) {
            return R.error(-1, "提现金额不能小于" + minAmount);
        }
        String maxAmountStr = configService.selectConfigByKey("send_porter_withMax");
        BigDecimal maxAmount = maxAmountStr == null ? new BigDecimal(49999) : new BigDecimal(maxAmountStr);
        if (new BigDecimal(amount).compareTo(maxAmount) > 0) {
            return R.error(-1, "提现金额不能大于" + maxAmount);
        }
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(amount));
        ordersEntity.setSendAmount(new BigDecimal(amount));
        ordersEntity.setSendUserId(userId);
        ordersEntity.setSendAccountName(accountName);
        ordersEntity.setSendAccountNo(accountNo);
        ordersEntity.setSendBankName(bankName);
        ordersEntity.setOrderType(OrdersEntityEnum.OrderType.PORTER_WITHDROW.getValue());
        ordersEntity.setOrderState(OrdersEntityEnum.OrderState.INIT.getValue());
        ordersEntity.setOrderDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        ordersEntity.setPayType(OrdersEntityEnum.PayType.BANK.getValue());
        ordersEntity.setIsApi(0);
        //搬运工提现费率
        String sendRate = configService.selectConfigByKey("send_porter_withRate");
        ordersEntity.setSendRate(sendRate == null ? new BigDecimal(0) : new BigDecimal(sendRate));
        BigDecimal sa = ordersEntity.getSendAmount().multiply(ordersEntity.getSendRate()).setScale(2, BigDecimal.ROUND_DOWN);
        ordersEntity.setSendRateAmount(sa);
        ordersEntity.setPlatDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if (r > 0) {
            //更改账户可用余额和冻结金额
            int r1 = accountService.updateAmount(userId, new BigDecimal(amount).negate(), new BigDecimal(amount), null);
            if (r1 <= 0) {
                throw new RRException("更改账户金额异常");
            }
        }
        return R.ok(ordersEntity);
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
        List<Map> orders = ordersDao.getOrders(param);
        if (orders != null && orders.size() > 0) {
            return R.error(-1, "有进行中的订单，稍后重试");
        }
        //查询搬运工充值相关配置
        String minAmountStr = configService.selectConfigByKey("send_porter_chargeMin");
        BigDecimal minAmount = minAmountStr == null ? new BigDecimal(3000) : new BigDecimal(minAmountStr);
        if (new BigDecimal(amount).compareTo(minAmount) < 0) {
            return R.error(-1, "充值金额不能小于" + minAmount);
        }
        String maxAmountStr = configService.selectConfigByKey("send_porter_chargeMax");
        BigDecimal maxAmount = maxAmountStr == null ? new BigDecimal(49999) : new BigDecimal(maxAmountStr);
        if (new BigDecimal(amount).compareTo(maxAmount) > 0) {
            return R.error(-1, "充值金额不能大于" + maxAmount);
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
        //订单超时时间
        String timeOutRecv = configService.selectConfigByKey("porter_charge_timeout_recv");
        String timeOutPay = configService.selectConfigByKey("porter_charge_timeout_pay");
        ordersEntity.setTimeoutRecv(timeOutRecv == null ? 30 : Integer.parseInt(timeOutRecv));
        ordersEntity.setTimeoutPay(timeOutPay == null ? 600 : Integer.parseInt(timeOutPay));
        ordersEntity.setIsApi(0);
        //搬运工充值发单费率、搬运工充值接单费率
        String sendRate = configService.selectConfigByKey("send_porter_chargeRate");
        String recvRate = configService.selectConfigByKey("recv_porter_chargeRate");
        ordersEntity.setSendRate(sendRate == null ? new BigDecimal(0) : new BigDecimal(sendRate));
        ordersEntity.setRecvRate(recvRate == null ? new BigDecimal(0) : new BigDecimal(recvRate));
        BigDecimal sa = ordersEntity.getSendAmount().multiply(ordersEntity.getSendRate()).setScale(2, BigDecimal.ROUND_DOWN);
        ordersEntity.setSendRateAmount(sa);
        BigDecimal ra = ordersEntity.getSendAmount().multiply(ordersEntity.getRecvRate()).setScale(2, BigDecimal.ROUND_DOWN);
        ordersEntity.setRecvRateAmount(ra);
        ordersEntity.setPlatDate(DateUtils.format(new Date(), DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if (r > 0) {
            // websocket 推送
            RushOrderInfo rushOrderInfo = new RushOrderInfo(
                    ordersEntity.getOrderId(),
                    ordersEntity.getOrderSn(),
                    ordersEntity.getCreateTime(),
                    ordersEntity.getOrderType(),
                    ordersEntity.getTimeoutRecv(),
                    ordersEntity.getPayType(),
                    ordersEntity.getSendAmount());
            RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.DISTRIBUTE_ORDER, System.currentTimeMillis(), rushOrderInfo);
            iRedisService.sendMessageToQueue(messageDomain);
        }
        return R.ok(ordersEntity);
    }

    @Override
    public boolean addOrder(OrdersEntity ordersEntity) {
        ordersEntity.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        return this.save(ordersEntity);
    }


    @Override
    public List<Map> getOrders(Map<String, Object> param) {
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

    @Override
    public void batchPushOrderToUser(boolean async, String mobile, int orderType) {
        if (async) {
            asyncBatchPushOrderToUser(mobile, orderType);
        } else {
            syncBatchPushOrderToUser(mobile, orderType);
        }
    }

    @Async
    void asyncBatchPushOrderToUser(String mobile, int orderType) {
        syncBatchPushOrderToUser(mobile, orderType);
    }

    public void syncBatchPushOrderToUser(String mobile, int orderType) {
        // 1 订单拉取
        String redisKey = RedisCacheKeyConstant.ORDER_LIST_CAN_BUY_PREFIX + mobile + ":" + orderType;
        long ordersNum = iRedisService.listSize(redisKey);
        log.info("订单下发开始，用户[{}] ExistNum[{}] MaxAllowNum[{}]", mobile, ordersNum, renrenProperties.getBatchPushOrderNumMax());
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

        log.info("推送[ {} ]订单开始,users_online[{}] users_CanRushBuy[{}] ...", orderType.getName(), /*WebSocketActionTypeEnum.PULL_ORDER.getDescribe(),*/ onlineUserWithMobile.size(), userSetCanRushBuy.size());
        // 同 handleWebSocketRequest.handleWebSocketRequest: ACTIVE & BEGIN_RECEIPT 处理
        if (CollectionUtils.isEmpty(onlineUserWithMobile) || CollectionUtils.isEmpty(userSetCanRushBuy)) {
            return;
        }

        //2、给有效用户推送指定类型的订单
        onlineUserWithMobile.stream().filter(v -> userSetCanRushBuy.contains(v)).forEach(v -> {
            batchPushOrderToUser(false, v, orderType.getValue());
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
     * 抢单成功:更新订单为接单成功状态 需要事务处理
     */

    public Map reciveOrderSuccess(Integer recvUserId, String orderType, String orderId) {
        Map returnMap = null;
        //首先查询订单
        OrdersEntity order = this.getById(orderId);
        if (order == null) {
            return returnMap;
        }
        //接单用户校验
        UserEntity user = userService.getById(recvUserId);
        if (user == null || user.getStatus() != 1) {
            return returnMap;
        }
        //校验账户
        AccountEntity accountEntity = accountService.getByUserId(recvUserId);
        if (accountEntity == null) {
            return returnMap;
        }
        //账户状态
        if (accountEntity.getStatus() != 1 || accountEntity.getActiveStatus() != 1 || accountEntity.getRecvStatus() != 1) {
            return returnMap;
        }
        //账户金额
        if (accountEntity.getCanuseAmount().compareTo(order.getSendAmount()) < 0) {
            return returnMap;
        }
        if (order.getOrderType() != Integer.parseInt(orderType)) {
            return returnMap;
        }
        //判断订单类型
        if (OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue() != Integer.parseInt(orderType)
                && OrdersEntityEnum.OrderType.MER_WITHDROW.getValue() != Integer.parseInt(orderType)
                && OrdersEntityEnum.OrderType.MER_RECHARGE.getValue() != Integer.parseInt(orderType)) {
            return returnMap;
        }
        //判断状态是否是待接单
        if (OrdersEntityEnum.OrderState.b.getValue() != order.getOrderState()) {
            return returnMap;
        }
        //查询接单用户支付方式是否包含订单支付类型
        Map<String, Object> params = new HashMap<>();
        params.put("userId", recvUserId);
        params.put("payType", order.getPayType());
        params.put("useStatus", 1);
        params.put("bindStatus", 1);
        List<PayChannelDetail> payChannelList = payChannelService.getPayChannels(params);
        if (payChannelList == null || payChannelList.size() < 1) {
            return returnMap;
        }
        //随机选择一个
        Random random = new Random();
        int n = random.nextInt(payChannelList.size());
        PayChannelDetail choosePayChannel = payChannelList.get(n);
        //抢单成功，更新订单和账户信息
        OrdersEntity updateOrder = SpringContextUtils.getBean(OrdersServiceImpl.class).reciveOrderSuccessTransactional(recvUserId, choosePayChannel, order);
        if (updateOrder != null) {
            //添加orders_log
            OrdersLogEntity ordersLogEntity = new OrdersLogEntity();
            ordersLogEntity.setOrderId(order.getOrderId());
            ordersLogEntity.setOrderState(OrdersEntityEnum.OrderState.c.getValue());
            ordersLogEntity.setPayType(order.getPayType());
            ordersLogEntity.setAmount(order.getAmount());
            ordersLogEntity.setRecvAmount(order.getRecvAmount());
            ordersLogEntity.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
            ordersLogService.save(ordersLogEntity);
        }
        returnMap = new HashMap();
        returnMap.put("order", updateOrder);
        returnMap.put("payChannel", choosePayChannel);
        return returnMap;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrdersEntity reciveOrderSuccessTransactional(Integer recvUserId, PayChannelDetail payChannel, OrdersEntity order) {
        //OrdersEntity updateOrder = new OrdersEntity();
        //updateOrder.setOrderId(order.getOrderId());
        order.setOrderState(OrdersEntityEnum.OrderState.c.getValue());
        order.setRecvUserId(recvUserId);
        if (OrdersEntityEnum.PayType.BANK.getValue().equals(order.getPayType())) {
            //银行卡转账
            order.setRecvAccountName(payChannel.getAccountName());
            order.setRecvAccountNo(payChannel.getAccountNo());
            order.setRecvBankName(payChannel.getBankName());
        } else {
            order.setRecvAccountName(payChannel.getAccountName());
            order.setRecvAccountNo(payChannel.getAccountNo());
            order.setQrimgId(payChannel.getQrimgId());
        }
        //更新订单信息
        int u1 = ordersDao.reciveOrderSuccess(order);
        if (u1 <= 0) {
            return null;
        }
        //更新账户金额
        int u2 = accountService.updateAmount(recvUserId, order.getSendAmount().negate(), order.getSendAmount(), new BigDecimal(0));
        if (u2 <= 0) {
            throw new RRException("抢单失败");
        }
        //更新接单用户账户日志信息
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(recvUserId, order.getOrderId(),
                1, "out", order.getSendAmount());
        return order;
    }

    /**
     * 充值确认：确认收款
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public R sureRecvOrder(Integer orderId, BigDecimal confirmAmount) {
        //查询订单状态
        OrdersEntity retOrdersEntity = SpringContextUtils.getBean(OrdersServiceImpl.class).getById(orderId);
        if (retOrdersEntity.getOrderState() == 9) {
            return R.ok();
        }
        if (retOrdersEntity.getOrderState() != 2 && retOrdersEntity.getOrderState() != 5) {
            return R.error(-1, "订单状态错误，提交失败");
        }
        if (retOrdersEntity.getAmount().compareTo(confirmAmount) != 0) {
            return R.error(-1, "请确认收款金额是否一致");
        }
        //业务事务处理
        retOrdersEntity.setRecvAmount(confirmAmount);

        boolean result = SpringContextUtils.getBean(OrdersServiceImpl.class).sureRecvOrderTransactional(retOrdersEntity);
        if (result) {

            return R.ok();
        } else {
            return R.error();
        }
    }

    /**
     * 充值确认：更新订单和账户
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean sureRecvOrderTransactional(OrdersEntity ordersEntity) {
        //更新订单信息
        OrdersEntity updateOrder = new OrdersEntity();
        updateOrder.setOrderId(ordersEntity.getOrderId());
        updateOrder.setOrderState(9);
        updateOrder.setRecvAmount(ordersEntity.getRecvAmount());
        boolean boo = SpringContextUtils.getBean(OrdersServiceImpl.class).updateById(updateOrder);
        if (!boo) {
            return false;
        }
        //1 发单用户
        //更新发单者账户信息,给账户 （扣除手续费后） 增加金额，及可用金额
        BigDecimal sendUserChangeAmount = ordersEntity.getAmount().subtract(ordersEntity.getSendRateAmount());
        int su = accountService.updateAmount(ordersEntity.getSendUserId(), sendUserChangeAmount, null, sendUserChangeAmount);
        // 更新发单者账户日志表，记录两笔
        //收入,充值
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(ordersEntity.getSendUserId(), ordersEntity.getOrderId(),
                4, "in", ordersEntity.getAmount().subtract(ordersEntity.getRecvAmount()));
        //费用,充值
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(ordersEntity.getSendUserId(), ordersEntity.getOrderId(),
                6, "out", ordersEntity.getSendRateAmount());

        if (su < 1) {
            throw new RRException("确认收款-更新发单者账户信息失败");
        }
        //2 收单用户


        // 更新接单者账户日志表，记录三笔
        //解冻
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(ordersEntity.getRecvUserId(), ordersEntity.getOrderId(),
                2, "in", ordersEntity.getRecvAmount());
        //更新接单者账户信息，解冻,扣减账户金额，及可用余额，增加获得的手续费
        BigDecimal recevUserChangeAmount = ordersEntity.getAmount().subtract(ordersEntity.getRecvRateAmount());
        int ru = accountService.updateAmount(ordersEntity.getRecvUserId(), ordersEntity.getRecvRateAmount(), ordersEntity.getAmount().negate(), recevUserChangeAmount.negate());
        //付出
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(ordersEntity.getRecvUserId(), ordersEntity.getOrderId(),
                3, "out", ordersEntity.getRecvAmount());
        //收益
        SpringContextUtils.getBean(AccountLogService.class).addAccountLog(ordersEntity.getRecvUserId(), ordersEntity.getOrderId(),
                5, "in", ordersEntity.getRecvRateAmount());
        if (ru < 1) {
            throw new RRException("确认收款-更新接单者账户信息失败");
        }
        return true;
    }


    @Override
    public List<Map<String, Object>> listValidOrders(List<Integer> typeList, List<Integer> stateList, List<Integer> excludeStatusList, int limit) {
        List<Map<String, Object>> rList = ordersDao.listValidOrders(typeList, stateList, excludeStatusList, limit);
        if (!CollectionUtils.isEmpty(rList)) {
            // TODO  订单处理
        }
        return rList;
    }

    // 1 查询已超时的订单包括 下单超时，支付超时，接单超时

    /**
     * 订单超时处理 ，目前处理的订单类型[ORDER_TYPE]：1 搬运工充值3 商户充值 4 商户提现
     * //        按照：TIMEOUT_RECV接单超时、TIMEOUT_PAY支付超时、和当前ORDER_STATE状态进行清理
     * //        0、如果订单类型不是4、6、9、30、31，则执行以下：
     * //        1、 如果当前时间超过“接单超时”时间,且订单状态是0、或1，则认为订单失败，更新订单为4。
     * //        3、清理掉队列中的相关订单。
     */

    @Override
    public void execOrderTimeOutHandle(int handleType) {
        List<Map<String, Object>> validOrderList;
        int rNum = 0;
        final int[] handleNum = {0};
        switch (handleType) {
            case 1: // 下单超时处理
                // todo 下单超时处理
                break;
            case 2: // 接单超时处理
                validOrderList = listValidOrders(Arrays.asList(1, 3, 4), Arrays.asList(0, 1), null, 10);
                if (!CollectionUtils.isEmpty(validOrderList)) {
                    rNum = validOrderList.size();
                    handleNum[0] = ordersDao.batchUpdateState(validOrderList.stream().map(v -> (Integer) v.get("orderId"))/*.mapToInt(x -> x)*/.collect(Collectors.toList()), 4, Arrays.asList(0, 1));
                }
                log.info("接单超时处理completely[{}]: [{}/{}]条。", handleNum[0] == rNum, handleNum[0], rNum);
                break;
            case 3: // 支付超时处理
                validOrderList = listValidOrders(Arrays.asList(1, 3, 4), Arrays.asList(2, 5), null, 10);

                if (!CollectionUtils.isEmpty(validOrderList)) {
                    rNum = validOrderList.size();
                    // 不用批处理，便于控制订单下发
                    validOrderList.forEach(v -> {
                        Integer orderId = (Integer) v.get("orderId");
                        String orderSn = (String) v.get("orderSn");
                        int fNum = ordersDao.batchUpdateState(Arrays.asList(orderId), 6, Arrays.asList(2, 5));
                        if (fNum == 1) {
                            // 下发通知至client
                            String mobile = (String) v.get("recvUserMobile");
                            log.info("下发超时订单[id-{},orderSn-{}]至用户[{}].", orderId, orderSn, mobile);
                            WebSocketResponseDomain webSocketResponseDomain = new WebSocketResponseDomain(WebSocketActionTypeEnum.ORDER_TIMEOUT_PAY.getCommand());
                            webSocketResponseDomain.setData(new BaseOrderInfo(orderId, orderSn, (String) v.get("createTime"), 6));
                            iNettyService.asyncSendMessage(mobile, webSocketResponseDomain);
                            handleNum[0]++;
                        }
                    });
                    log.info("支付超时处理completely[{}]: [{}/{}]条。", handleNum[0] == rNum, handleNum[0], rNum);
                }
                break;
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public R withdrawAudit(OrdersEntity ordersEntity, String auditStatus, String remark) {
        if(ordersEntity == null){
            return R.error();
        }
        if(auditStatus.equals("Y")){
            //更新账户
            int u = accountService.updateAmount(ordersEntity.getSendUserId(),null,ordersEntity.getSendAmount().negate(),ordersEntity.getSendAmount().negate());
            if(u > 0){
                //更新订单
                OrdersEntity updateOrder = new OrdersEntity();
                updateOrder.setOrderId(ordersEntity.getOrderId());
                updateOrder.setOrderState(9);
                updateOrder.setRemark(remark);
                u = ordersDao.withdrawAudit(updateOrder);
                if(u <= 0){
                    throw new RRException("withdrawAudit fail");
                }
            }
            //审核通过
        }else if(auditStatus.equals("N")){
            //审核不通过
            OrdersEntity updateOrder = new OrdersEntity();
            updateOrder.setOrderId(ordersEntity.getOrderId());
            updateOrder.setOrderState(31);
            updateOrder.setRemark(remark);
            int u1 = ordersDao.withdrawAudit(updateOrder);
            if(u1 > 0){
                //更新account
                u1 = accountService.updateAmount(ordersEntity.getSendUserId(),ordersEntity.getSendAmount(),ordersEntity.getSendAmount().negate(),null);
                if(u1 <= 0){
                    throw new RRException("withdrawAudit fail");
                }
            }
        }
        return R.ok();
    }
}
