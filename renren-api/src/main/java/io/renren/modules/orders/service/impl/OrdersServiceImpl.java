package io.renren.modules.orders.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.orders.dao.OrdersDao;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Service("ordersService")
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {
    @Autowired
    IRedisService iRedisService;
    /**
     * 订单申请，创建:外围接口做必要的数据校验后调用本方法
     */
    public Map applyOrder(Integer merId, String orderDate,int orderType,
                          String orderSn, String payType, String sendAmount, String notifyUrl){
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
        boolean boo = this.save(orders);
        // TODO orders.getOrderId() 验证
        if(boo==true && (orders.getOrderId()!=null &&orders.getOrderId()>0)){//检查订单是否创建成功
            //发送到redis待抢单队列 商户id、订单编号、支付类型
            //TODO

            RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.DISTRIBUTE_ORDER, System.currentTimeMillis(), orders);
            iRedisService.sendMessageToQueue(messageDomain);

            Integer orderId = orders.getOrderId();
            returnMap.put("orders",orders);//返回订单
            returnMap.put("code",0);
            return returnMap;//添加成功
        }else {
            returnMap.put("code",-1);
            return returnMap;//添加失败
        }
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
    public Map selectOrder(String recvUserId,String orderType,String orderState){
        Map returnMap = new HashMap();

        //查询订单状态及信息


        //ridis事物提交


        return returnMap;
    }

    /**
     * 确认收款
     */
    public Map recvAmount(BigDecimal recvAmount,String orderType,String orderState){
        Map returnMap = new HashMap();

        //查询订单状态及信息


        //ridis事物提交


        return returnMap;
    }

    /**
     * 订单超时,系统取消
     */
    public Map orderTimeout(String recvUserId,String orderType,String orderState){
        Map returnMap = new HashMap();

        //{"a":45,"code":0,"order_id":2528445,"is_api":"1",
        // "order_sn":"414028","order_type":"1","msg":"\u8d85\u65f6\u672a\u4ed8\u6b3e\u7cfb\u7edf\u53d6\u6d88!"}

        return returnMap;
    }
    /**
     * 订单查询提醒：用户已付款，下发提醒
     */
    public Map orderRemind(String recvUserId,String orderType,String orderState){
        Map returnMap = new HashMap();

        //{"a":64,"code":0,"order_id":2654348}


        return returnMap;
    }


    //下发余额



}
