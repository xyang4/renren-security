package io.renren.modules.orders.controller;

import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;


/**
 * app订单接口
 */
@Slf4j
@RestController
@RequestMapping("app/orders")
public class OrdersController extends BaseController {
    @Autowired
    private OrdersService ordersService;


    @ApiOperation("搬运工充值申请")
    @RequestMapping("/hamalRecharge")
    public R hamalRecharge(@RequestParam(required = true,value = "rechargeAmount") String rechargeAmount,
                           String accountName,String accountNo){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //查询用户是否有进行中的充值
        Map<String,Object> param =new HashMap<>();
        param.put("sendUserId",tokenEntity.getUserId());
        param.put("orderType",OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        List<Integer> orderStates = new ArrayList<>();
        orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
        orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        param.put("includeState",orderStates);
        List<OrdersEntity> orders = ordersService.getOrders(param);
        if(orders != null && orders.size() > 0){
            return R.error(-1,"有进行中的订单，稍后重试");
        }
        OrdersEntity ordersEntity = new OrdersEntity();
        ordersEntity.setAmount(new BigDecimal(rechargeAmount));
        ordersEntity.setSendAmount(new BigDecimal(rechargeAmount));
        ordersEntity.setSendUserId(tokenEntity.getUserId());
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
        boolean r = ordersService.addOrder(ordersEntity);
        if(r){
            //todo websocket 推送
        }
        return R.ok();
    }


    @ApiOperation("搬运工查询充值进行中")
    @RequestMapping("/hamalRecharge/processing/list")
    public R hamalRechargeProcessingList(){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Map<String,Object> param =new HashMap<>();
        param.put("sendUserId",tokenEntity.getUserId());
        param.put("orderType",OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue());
        List<Integer> orderStates = new ArrayList<>();
        orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
        orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        param.put("includeState",orderStates);
        List<OrdersEntity> orders = ordersService.getOrders(param);
        return R.ok(orders);
    }

}
