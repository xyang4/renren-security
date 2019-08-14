package io.renren.modules.orders.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.form.HamalOrderForm;
import io.renren.modules.orders.form.OrderPageForm;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.system.service.ISmsService;
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
    @Autowired
    ISmsService iSmsService;

    @AppLogin
    @ApiOperation("搬运工充值申请")
    @RequestMapping("/hamalRecharge")
    public R hamalRecharge(@RequestBody HamalOrderForm hamalOrderForm){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //短信验证码校验
        boolean check = iSmsService.validCode(tokenEntity.getMobile(), hamalOrderForm.getSmsCode());
        //TODO 临时关闭
        //        if(!check){
//            return R.error(-1,"验证码错误");
//        }
        return ordersService.hamalRecharge(tokenEntity.getUserId(),hamalOrderForm.getAmount(),hamalOrderForm.getAccountName(),hamalOrderForm.getAccountNo());
    }

    @AppLogin
    @ApiOperation("搬运工提现申请")
    @RequestMapping("/hamalWithdraw")
    public R hamalWithdraw(@RequestBody HamalOrderForm hamalOrderForm){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //短信验证码校验
//        boolean check = iSmsService.validCode(tokenEntity.getMobile(), hamalOrderForm.getSmsCode());
//        if(!check){
//            return R.error(-1,"验证码错误");
//        }
        return ordersService.hamalWithdraw(tokenEntity.getUserId(),hamalOrderForm.getAmount(),hamalOrderForm.getAccountName(),hamalOrderForm.getAccountNo(),hamalOrderForm.getBankName());
    }

    /**
     *
     * @param orderTypeMap(orderType) recharge:充值，withdraw:提现
     * @return
     */
    @AppLogin
    @ApiOperation("首页进行中查询")
    @RequestMapping("/hamal/indexprocessingOrderList")
    public R indexprocessingOrderList(@RequestBody Map orderTypeMap){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Map<String,Object> param =new HashMap<>();
        param.put("orderType",1);

        List<Integer> orderStates = new ArrayList<>();
        param.put("recvUserId",tokenEntity.getUserId());
        orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
        orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        orderStates.add(8);

        param.put("includeState",orderStates);
        List<Map> orders = new ArrayList<>();
        List<Map> ordersRet = ordersService.getOrders(param);
        if(ordersRet!=null){
            orders = ordersRet;
        }
        Map<String,Object> param2 =new HashMap<>();
        param2.put("orderType",3);
        param2.put("recvUserId",tokenEntity.getUserId());
        List<Integer> orderStates2 = new ArrayList<>();
        orderStates2.add(OrdersEntityEnum.OrderState.c.getValue());
        param2.put("includeState",orderStates2);
        List<Map> orders2 = ordersService.getOrders(param2);
        if(orders2!=null){
            orders.addAll(orders2);
        }
        return R.ok(orders);
    }

    /**
     *
     * @param orderTypeMap(orderType) recharge:充值，withdraw:提现
     * @return
     */
    @AppLogin
    @ApiOperation("搬运工充值、提现进行中查询")
    @RequestMapping("/hamal/processingOrderList")
    public R hamalprocessingOrderList(@RequestBody Map orderTypeMap){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer orderType = (Integer) orderTypeMap.get("orderType");
        if(orderType == null){
            return R.error(-1001,"请求参数错误");
        }
        Map<String,Object> param =new HashMap<>();
        param.put("orderType",orderType);

        List<Integer> orderStates = new ArrayList<>();
        if(orderType == OrdersEntityEnum.OrderType.PORTER_RECHARGE.getValue()){
            param.put("sendUserId",tokenEntity.getUserId());
            orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
            orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
            orderStates.add(8);
        }else if(orderType == OrdersEntityEnum.OrderType.PORTER_WITHDROW.getValue()){
            param.put("sendUserId",tokenEntity.getUserId());
            orderStates.add(OrdersEntityEnum.OrderState.INIT.getValue());
        }else if(orderType == OrdersEntityEnum.OrderType.MER_RECHARGE.getValue()){
            param.put("recvUserId",tokenEntity.getUserId());
            orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        }else{
            return R.ok();
        }
        param.put("includeState",orderStates);
        List<Map> orders = ordersService.getOrders(param);
        return R.ok(orders);
    }


    @AppLogin
    @ApiOperation("查询订单列表")
    @RequestMapping("/pageList")
    public R getOrdersList(@RequestBody OrderPageForm orderPageForm){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        orderPageForm.setUserId(tokenEntity.getUserId());
        List<OrdersEntity> orders = ordersService.getSendOrRecvOrderList(orderPageForm);
        return R.ok(orders);
    }

    @AppLogin
    @ApiOperation("订单详情")
    @RequestMapping("/detail")
    public R detail(@RequestBody Map map){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer orderId = (Integer)map.get("orderId");
        if(orderId == null){
            return R.error(-1001,"请求参数错误");
        }
        //检查订单是否是自己的
        OrdersEntity ordersEntity= ordersService.getById(orderId);
//        if(ordersEntity.getRecvUserId().equals(tokenEntity.getUserId())){
//            log.error("告警：订单不是该用户的订单UserId {} orderId{}",tokenEntity.getUserId(),orderId);
//            return R.error(-1,"订单不合法");
//        }

        return R.ok(ordersEntity);
    }

    @AppLogin
    @ApiOperation("发单确认付款")
    @RequestMapping("/sureSendAmount")
    public R sureSendAmount(@RequestBody Map map){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer orderId = (Integer)map.get("orderId");
        if(orderId == null){
            return R.error(-1001,"请求参数错误");
        }
        //检查订单是否是自己的
//        OrdersEntity ordersEntity= ordersService.getById(orderId);
//        if(ordersEntity.getSendUserId().equals(tokenEntity.getUserId())){
//            log.error("告警：发单确认付款,订单不是该用户的订单UserId {} orderId{}",tokenEntity.getUserId(),orderId);
//            return R.error(-1,"订单不合法");
//        }
        //确认收款
        return ordersService.sureSendAmount(orderId);
    }


    @AppLogin
    @ApiOperation("订单收款确认")
    @RequestMapping("/sureRecvOrder")
    public R sureRecvOrder(@RequestBody Map map){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer orderId = (Integer)map.get("orderId");
        //检查订单是否是自己的
        OrdersEntity ordersEntity= ordersService.getById(orderId);
        //商户充值订单
        if(ordersEntity.getOrderType()==3 &&
                ordersEntity.getRecvUserId().intValue()!=tokenEntity.getUserId().intValue()){
            log.error("告警：商户充值订单收款确认,订单不是该用户的订单UserId {} orderId{}",tokenEntity.getUserId(),orderId);
            return R.error(-1,"商户充值订单不合法");
        }
        //搬运工充值
        if(ordersEntity.getOrderType()==1 &&
                ordersEntity.getRecvUserId().intValue()!=tokenEntity.getUserId().intValue()){
            log.error("告警：搬运工充值订单收款确认,订单不是该用户的订单UserId {} orderId{}",tokenEntity.getUserId(),orderId);
            return R.error(-1,"搬运工充值不合法");
        }
        BigDecimal confirmAmount = BigDecimal.valueOf(Double.parseDouble((String)map.get("confirmAmount")));
        if(orderId == null){
            return R.error(-1001,"请求参数错误");
        }
        //确认收款
        return ordersService.sureRecvOrder(orderId,confirmAmount);
    }


    @AppLogin
    @ApiOperation("搬运工提现申请审核")
    @RequestMapping("/withdrawAudit")
    public R withdrawAudit(Integer orderId,String auditStatus,String remark){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        OrdersEntity ordersEntity = ordersService.getById(orderId);
        if(ordersEntity == null){
            return R.error(-11,"订单不存在");
        }
        if(ordersEntity.getOrderType() != OrdersEntityEnum.OrderType.PORTER_WITHDROW.getValue()){
            return R.error(-11,"订单类型错误");
        }
        if(ordersEntity.getOrderState() != OrdersEntityEnum.OrderState.INIT.getValue()){
            return R.error(-11,"订单状态错误");
        }
        return ordersService.withdrawAudit(ordersEntity,auditStatus,remark);
    }

}
