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
public class RushOrderInfo {
    private String a;
    private String orderSn;
    private String createTime;
    private Integer orderType;
    private Integer timeoutRecv;

}
