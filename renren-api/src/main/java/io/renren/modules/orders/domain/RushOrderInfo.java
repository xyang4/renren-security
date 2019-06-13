package io.renren.modules.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public RushOrderInfo(Integer orderId, String orderSn, String createTime, Integer orderType, Integer timeoutRecv, String payType) {
        this.orderId = orderId;
        this.orderSn = orderSn;
        this.createTime = createTime;
        this.orderType = orderType;
        this.timeoutRecv = timeoutRecv;
        this.payType = payType;
    }

}
