package io.renren.modules.orders.domain;

import lombok.Data;

/**
 * 订单规则
 */
@Data
public class OrderRule {
    double minAmount;
    double maxAmount;
}
