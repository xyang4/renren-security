package io.renren.modules.orders.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单基本信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseOrderInfo {
    protected Integer orderId;
    protected String orderSn;
    protected String createTime;
    protected Integer state;
}
