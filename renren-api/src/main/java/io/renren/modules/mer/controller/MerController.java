package io.renren.modules.mer.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.enums.OrdersEntityEnum;
import io.renren.common.utils.R;
import io.renren.modules.mer.service.MerService;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("mer")
@Slf4j
@Api(tags = "商户接口")
public class MerController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    MerService merService;
    /**
     * 商户充值订单预申请
     */
    @PostMapping("/order/applyRecharge")
    @ApiOperation("商户充值订单预申请")
    @ResponseBody
    public R applyRecharge(HttpServletRequest request,
                           @RequestParam(value="merId",required=true) Integer merId,
                           @RequestParam(value="orderDate",required=true) String orderDate,
                           @RequestParam(value="orderSn",required=true) String orderSn,
                           @RequestParam(value="payType",required=true) String payType,
                           @RequestParam(value="sendAmount",required=true) String sendAmount,
                           @RequestParam(value="notifyUrl",required=true) String notifyUrl){
        log.info("商户充值订单申请：merId：{},orderSn:{},payType:{},sendAmount:{},notifyUrl:{}",
                merId,orderSn,payType,sendAmount,notifyUrl);
        //校验payType类型
        if(!OrdersEntityEnum.PayType.contains(payType)){
            return R.error(400,"payType类型不合法");
        }
        //校验sendAmount发送金额
        if((Double.parseDouble(sendAmount))>50000 ||(Double.parseDouble(sendAmount))<100){
            return R.error(400,"金额超出范围");
        }
        //校验notifyUrl回调地址
        if(!notifyUrl.contains("http")){
            return R.error(400,"notifyUrl http地址不合法");
        }
        //校验orderSn是否已经存在
        Wrapper<OrdersEntity> queryWrapper = new QueryWrapper<>();
        ((QueryWrapper<OrdersEntity>) queryWrapper).eq("order_Sn",orderSn);
        OrdersEntity retOrdersEntity= ordersService.getOne(queryWrapper);
        if(retOrdersEntity!=null){
            return R.error(400,"orderSn已存在");
        }
        //创建预处理订单
        Map retMap= ordersService.applyOrder(merId, orderDate,3, orderSn,  payType,  sendAmount,  notifyUrl);
        if((int)retMap.get("code")==0){//返回成功，返回给冲值订单页面：带签名/orderId"
            OrdersEntity ordersEntity = (OrdersEntity)retMap.get("orders");
            Map merMap = new HashMap();
            // TODO
            //返回支付充值页面
            merMap.put("payUrl","http://localhost:8180"+"/api/mer/order/payIndex?orderId="
                    +ordersEntity.getOrderId()+"timeStamp="+"&sign=");
            return R.ok(merMap);
        }else {//返回失败
            return R.error();
        }
    }

    /**
     * 商户充值订单支付页面
     */
    @RequestMapping(value={"/order/payIndex"}, method = {RequestMethod.POST,RequestMethod.GET})
    public String payIndex(HttpServletRequest request,Model model,
                           @RequestParam(value="orderId",required=true) Integer orderId,
                           @RequestParam(value="timeStamp",required=true) String timeStamp,
                           @RequestParam(value="sign",required=true) String sign){
        //查询订单信息
        OrdersEntity ordersEntity = ordersService.getById(orderId);
        //校验订单状态
//        if(ordersEntity.getOrderState()){//超时返回
//            return "mer/error";
//        }
        //校验订单时间
//        if(ordersEntity.getTimeoutPay()){//超时返回
//          return "mer/error";
//        }

        Map ordersMap = new HashMap();
        ordersMap.put("orderSn",ordersEntity.getOrderSn());//商户订单orderSn
        ordersMap.put("createTime",ordersEntity.getCreateTime());//创建时间
        ordersMap.put("timeOut","");//订单超时时间秒数 TODO
        //原timeStamp+sign
        ordersMap.put("timeStamp",timeStamp);//timeStamp
        ordersMap.put("sign",sign);//sign
        model.addAttribute("pageMap",ordersMap);
        return "mer/payIndex";
    }

    /**
     * 商户充值订单抢单结果查询（查询被分配的支付付款通道）：页面ajax查询使用
     */
    @RequestMapping(value={"/order/selectRechargePayChannel"}, method = {RequestMethod.POST,RequestMethod.GET})
    public R selectRechargePayChannel(HttpServletRequest request,Model model,
                                      @RequestParam(value="orderId",required=true) String orderId,
                                      @RequestParam(value="timeStamp",required=true) String timeStamp){
        //查询订单信息
        OrdersEntity ordersEntity = ordersService.getById(orderId);
        Map ordersMap = new HashMap();
        ordersMap.put("orderSn",ordersEntity.getOrderSn());//商户订单orderSn
        ordersMap.put("createTime",ordersEntity.getCreateTime());//创建时间
        ordersMap.put("timeOut","");//订单超时时间秒数 TODO
        ordersMap.put("amount",ordersEntity.getAmount());//应支付金额
        //原timeStamp+sign
        ordersMap.put("timeStamp",timeStamp);//timeStamp

        model.addAttribute("pageMap",ordersMap);

        return R.ok();
    }


    /**
     * 商户查询订单状态：充值，提现
     */
    @RequestMapping(value={"/order/selectOrderStatus"}, method = {RequestMethod.POST,RequestMethod.GET})
    public R selectOrderStatus(HttpServletRequest request,
                           @RequestParam(value="merId",required=true) Integer merId,
                           @RequestParam(value="orderSn",required=true) String orderSn){
        //查询订单信息
        Wrapper<OrdersEntity> ordersEntityQuery = new QueryWrapper<>();
        ((QueryWrapper<OrdersEntity>) ordersEntityQuery).eq("send_user_id",merId);
        ((QueryWrapper<OrdersEntity>) ordersEntityQuery).eq("orderSn",orderSn);
        OrdersEntity ordersEntity = ordersService.getOne(ordersEntityQuery);

        Map ordersMap = new HashMap();
        ordersMap.put("merId",ordersEntity.getSendUserId());//商户号
        ordersMap.put("orderSn",ordersEntity.getOrderSn());//商户订单orderSn
        ordersMap.put("amount",ordersEntity.getAmount());//应支付金额
        ordersMap.put("orderState",ordersEntity.getOrderState());//订单状态

        return R.ok();
    }


}
