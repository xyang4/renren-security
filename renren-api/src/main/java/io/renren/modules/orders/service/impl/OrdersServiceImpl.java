package io.renren.modules.orders.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.orders.dao.OrdersDao;
import io.renren.modules.orders.domain.OrderRule;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.form.OrderPageForm;
import io.renren.modules.orders.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {
    @Autowired
    IRedisService iRedisService;

    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private AccountService accountService;

    @Override
    public List<Map<String, Object>> receiveValidOrder(String mobile, OrderRule orderRule, int size) {
        //        TODO
        return null;
    }

    @Override
    public Map<String, Object> rushToBuy(String mobile, String orderSn) {

        //        TODO
        return null;
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
        orders.setOrderDate(orderDate);
        orders.setOrderType(orderType);
        orders.setOrderSn(orderSn);//商户原始订单
        orders.setPayType(payType);//付款类型
        orders.setSendAmount(new BigDecimal(sendAmount));//发送金额
        orders.setNotifyUrl(notifyUrl);//回调地址
        orders.setTimeoutRecv(30);//抢单超时时间，秒 TODO
        boolean boo = this.save(orders);
        //验证
        if (boo == true && (orders.getOrderId() != null && orders.getOrderId() > 0)) {//检查订单是否创建成功
            //发送到redis待抢单队列 商户id、订单编号、支付类型 TODO
            RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.PUSH_ORDER_TO_SPECIAL_USER, System.currentTimeMillis(), orders);
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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public R hamalWithdraw(Integer userId, String amount, String accountName, String accountNo) {
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(amount));
        ordersEntity.setSendAmount(new BigDecimal(amount));
        ordersEntity.setSendUserId(userId);
        ordersEntity.setSendAccountName(accountName);
        ordersEntity.setSendAccountNo(accountNo);
        ordersEntity.setOrderType(OrdersEntityEnum.OrderType.PORTER_WITHDROW.getValue());
        ordersEntity.setOrderState(OrdersEntityEnum.OrderState.INIT.getValue());
        ordersEntity.setOrderDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
        ordersEntity.setPayType(OrdersEntityEnum.PayType.BANK.getValue());
        ordersEntity.setIsApi(0);
        ordersEntity.setPlatDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if(r > 0){
            //更改账户可用余额和冻结金额
            int r1 = accountService.updateAmount(userId,new BigDecimal(amount).negate(),new BigDecimal(amount));
            if(r1 <= 0){
                throw new RRException("更改账户金额异常");
            }
        }
        return R.error();
    }

    @Override
    public R hamalRecharge(Integer userId, String amount, String accountName, String accountNo) {
        //查询用户是否有进行中的充值
        Map<String,Object> param =new HashMap<>();
        param.put("sendUserId",userId);
        param.put("orderType",OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        List<Integer> orderStates = new ArrayList<>();
        orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
        orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        param.put("includeState",orderStates);
        List<OrdersEntity> orders = ordersDao.getOrders(param);
        if(orders != null && orders.size() > 0){
            return R.error(-1,"有进行中的订单，稍后重试");
        }
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(amount));
        ordersEntity.setSendAmount(new BigDecimal(amount));
        ordersEntity.setSendUserId(userId);
        ordersEntity.setSendAccountName(accountName);
        ordersEntity.setSendAccountNo(accountNo);
        ordersEntity.setOrderType(OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        ordersEntity.setOrderState(OrdersEntityEnum.OrderState.b.getValue());
        ordersEntity.setOrderDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
        ordersEntity.setPayType(OrdersEntityEnum.PayType.BANK.getValue());
        //todo 订单超时时间
        ordersEntity.setTimeoutRecv(60);
        ordersEntity.setTimeoutDown(120);
        ordersEntity.setIsApi(0);
        ordersEntity.setPlatDate(DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
        int r = ordersDao.insert(ordersEntity);
        if(r > 0){
            //todo websocket 推送
        }
        return R.ok();
    }

    @Override
    public boolean addOrder(OrdersEntity ordersEntity) {
        ordersEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        return this.save(ordersEntity);
    }


    @Override
    public List<OrdersEntity> getOrders(Map<String, Object> param) {
        return ordersDao.getOrders(param);
    }

    @Override
    public List<OrdersEntity> getSendOrRecvOrderList(OrderPageForm orderPageForm) {
        Map<String,Object> param =new HashMap<>();
        param.put("userId",orderPageForm.getUserId());
        param.put("orderType",orderPageForm.getOrderType());
        param.put("orderState",orderPageForm.getOrderState());
        //默认第一页5条
        Integer pageIndex = orderPageForm.getPageIndex()==null?1:orderPageForm.getPageIndex();
        Integer pageSize = orderPageForm.getPageSize()==null?5:orderPageForm.getPageSize();
        Page<OrdersEntity> ordersPage = new Page<>(pageIndex,pageSize);
        ordersPage.setRecords(ordersDao.getSendOrRecvOrderList(ordersPage,param));
        return ordersPage.getRecords();
    }

    @Override
    public boolean checkValidity(Object orderInfo) {
        boolean r = true;
        if (orderInfo instanceof OrdersEntity) {
            // TODO

        } else if (orderInfo instanceof String) {
            // 同 RedisMessageReceiver.onMessage : PUSH_ORDER_TO_SPECIAL_USER 处理
            return true;
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


    //下发余额


}
