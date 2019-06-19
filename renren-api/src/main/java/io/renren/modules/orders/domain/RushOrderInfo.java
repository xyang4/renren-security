package io.renren.modules.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 抢单信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RushOrderInfo extends BaseOrderInfo {

    private Integer orderType;
    private Integer timeoutRecv;
    private String payType;
    private BigDecimal sendAmount;

    public RushOrderInfo(Integer orderId, String orderSn, String createTime, Integer orderType, Integer timeoutRecv, String payType, BigDecimal sendAmount) {
        this.orderId = orderId;
        this.orderSn = orderSn;
        this.createTime = createTime;
        this.orderType = orderType;
        this.timeoutRecv = timeoutRecv;
        this.payType = payType;
        this.sendAmount = sendAmount;
    }

}
