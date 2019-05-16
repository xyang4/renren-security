package io.renren.modules.mer.controller;

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


        //校验sendAmount发送金额


        //校验notifyUrl回调地址

        //创建预处理订单
        Map retMap= ordersService.applyOrder(merId, orderDate,3, orderSn,  payType,  sendAmount,  notifyUrl);
        if((int)retMap.get("code")==0){//返回成功，返回给冲值订单页面：带签名/orderId"
            OrdersEntity ordersEntity = (OrdersEntity)retMap.get("orders");
            Map merMap = new HashMap();
            // TODO
            merMap.put("payUrl","http://"+"mer/payIndex?orderId="+ordersEntity.getOrderId()+"timeStamp="+"&sign=");
            return R.ok(merMap);
        }else {//返回失败
            return null;
        }

    }

    /**
     * 商户充值订单支付页面
     */
    @RequestMapping(value={"/order/payIndex"}, method = {RequestMethod.POST,RequestMethod.GET})
    public String payIndex(HttpServletRequest request,
                           @RequestParam(value="orderId",required=true) Integer orderId,
                           @RequestParam(value="timeStamp",required=true) String timeStamp,
                           @RequestParam(value="sign",required=true) String sign){
        // logger.info("商户充值订单申请：merId：{},orderSn :{} orderId:{}",
        //         merId,orderSn,orderId);

        //判断时间戳，超过10分钟认为订单超时，返回错误页面


        //查询订单信息
        OrdersEntity ordersEntity = ordersService.getById(orderId);
        Map ordersMap = new HashMap();
        ordersMap.put("orderSn",ordersEntity.getOrderSn());//商户订单orderSn
        ordersMap.put("createTime",ordersEntity.getCreateTime());//创建时间
        ordersMap.put("timeOut","");//订单超时时间秒数 TODO
        ordersMap.put("amount",ordersEntity.getAmount());//应支付金额
        //原timeStamp+sign
        ordersMap.put("timeStamp",timeStamp);//timeStamp
        ordersMap.put("sign",sign);//sign

        return "/mer/payIndex";
    }

    /**
     * 商户充值订单抢单结果查询（查询被分配的支付付款通道）：页面ajax查询使用
     */
    @RequestMapping(value={"/order/selectRechargePayChannel"}, method = {RequestMethod.POST,RequestMethod.GET})
    public R selectRechargePayChannel(HttpServletRequest request,
                                      @RequestParam(value="orderId",required=true) String orderId,
                                      @RequestParam(value="timeStamp",required=true) String timeStamp){
//        logger.info("商户充值订单申请：merId：{},orderSn:{},payType:{},sendAmount:{},notifyUrl:{}",
//                merId,orderSn,payType,sendAmount,notifyUrl);

        //判断时间戳，超过10分钟认为订单超时，返回错误页面



        return null;
    }


}
