package io.renren.modules.orders.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.form.HamalOrderForm;
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
        if(!check){
            return R.error(-1,"验证码错误");
        }
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
        boolean check = iSmsService.validCode(tokenEntity.getMobile(), hamalOrderForm.getSmsCode());
        if(!check){
            return R.error(-1,"验证码错误");
        }
        return ordersService.hamalWithdraw(tokenEntity.getUserId(),hamalOrderForm.getAmount(),hamalOrderForm.getAccountName(),hamalOrderForm.getAccountNo());
    }

    /**
     *
     * @param map(orderType) recharge:充值，withdraw:提现
     * @return
     */
    @AppLogin
    @ApiOperation("搬运工充值、提现进行中查询")
    @RequestMapping("/hamal/processingOrderList")
    public R hamalprocessingOrderList(@RequestBody Map map){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        String orderType = (String) map.get("orderType");
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
