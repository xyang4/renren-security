package io.renren.modules.orders.controller;

import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.system.service.ISmsService;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @ApiOperation("搬运工充值申请")
    @RequestMapping("/hamalRecharge")
    public R hamalRecharge(@RequestParam(required = true,value = "amount") String amount,
                           @RequestParam(required = true,value = "smsCode") String smsCode,
                           String accountName,String accountNo){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //短信验证码校验
        boolean check = iSmsService.validCode(tokenEntity.getMobile(), smsCode);
        if(!check){
            return R.error(-1,"验证码错误");
        }
        return ordersService.hamalRecharge(tokenEntity.getUserId(),amount,accountName,accountNo);
    }

    @ApiOperation("搬运工提现申请")
    @RequestMapping("/hamalWithdraw")
    public R hamalWithdraw(@RequestParam(required = true,value = "amount") String amount,
                           @RequestParam(required = true,value = "smsCode") String smsCode,
                           String accountName,String accountNo){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //短信验证码校验
        boolean check = iSmsService.validCode(tokenEntity.getMobile(), smsCode);
        if(!check){
            return R.error(-1,"验证码错误");
        }
        return ordersService.hamalWithdraw(tokenEntity.getUserId(),amount,accountName,accountNo);
    }

    /**
     *
     * @param orderType recharge:充值，withdraw:提现
     * @return
     */
    @ApiOperation("搬运工充值、提现进行中查询")
    @RequestMapping("/hamal/processingOrderList")
    public R hamalprocessingOrderList(
            @RequestParam(required = true,value = "orderType") String orderType){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        if(StringUtils.isBlank(orderType)){
            return R.error(-1001,"请求参数错误");
        }
        Map<String,Object> param =new HashMap<>();
        param.put("orderType",orderType);
        param.put("sendUserId",tokenEntity.getUserId());
        List<Integer> orderStates = new ArrayList<>();
        if("recharge".equals(orderType)){
            orderStates.add(OrdersEntityEnum.OrderState.b.getValue());
            orderStates.add(OrdersEntityEnum.OrderState.c.getValue());
        }else if("withdraw".equals(orderType)){
            orderStates.add(OrdersEntityEnum.OrderState.INIT.getValue());
        }else{
            return R.ok();
        }
        param.put("includeState",orderStates);
        List<OrdersEntity> orders = ordersService.getOrders(param);
        return R.ok(orders);
    }

}
