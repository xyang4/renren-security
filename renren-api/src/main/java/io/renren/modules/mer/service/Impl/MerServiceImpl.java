package io.renren.modules.mer.service.Impl;

import io.renren.common.enums.UserEntityEnum;
import io.renren.common.util.HttpClientUtil;
import io.renren.modules.mer.service.MerService;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("merService")
public class MerServiceImpl implements MerService {

    @Autowired
    OrdersService ordersService;
    @Autowired
    UserService userService;
    /**
     * 校验商户有效性
     */
    public boolean checkMer(Integer userId){
        UserEntity mer = userService.getById(userId);
        //用户状态有效、是商户类型
        if( mer!=null &&
                mer.getStatus() == UserEntityEnum.Status.VALID.getValue() &&
                mer.getUserType() == UserEntityEnum.UserType.MER.getValue()  ){
            return true;
        }
        return false;
    }

    /**
     * 异步通知商户订单结果
     */
    public boolean sendOrderNotify(OrdersEntity ordersEntity){
        if(ordersEntity.getOrderType()==3||ordersEntity.getOrderType()==4){//商户提现或从充值
            //订单状态 0初始1-订单提交 通知抢单,待接单2-已接单3-用户取消4-超时未接单系统取消
            // 5-等待打款并确认6-超时未打款取消7-支付受限,重新派单8-发单确认打款
            // 9-收单确认已打款 ,订单完成15-等待打款--更换付款方式30-客服处理为取消31-客服处理为完成
            if(ordersEntity.getOrderState()==4||ordersEntity.getOrderState()==9){
                Map retMap = new HashMap<>();
                retMap.put("merId",ordersEntity.getSendUserId());//商户号
                retMap.put("orderId",ordersEntity.getOrderId());//平台订单号
                retMap.put("orderDate",ordersEntity.getOrderDate());//交易日期
                retMap.put("orderSn",ordersEntity.getOrderSn());//商户sn
                retMap.put("amount",ordersEntity.getAmount());//实际交易金额amount
                retMap.put("orderState",ordersEntity.getOrderState());//订单状态
                retMap.put("amount",ordersEntity.getAmount());//实际交易金额amount
                retMap.put("sign","");//签名
                String retString = HttpClientUtil.doGet(ordersEntity.getNotifyUrl(),retMap);
                //TODO 商户接收结果

                return true;
            }
        }
        return false;
    }



}
