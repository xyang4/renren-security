package io.renren.common.enums;

import lombok.Getter;

/**
 * 订单枚举
 */
public class OrdersEntityEnum {

    /**
     * 订单类型（1搬运工充值2搬运工提现3商户充值4商户提现）
     */
    @Getter
    public static enum OrderType{
        PORTER_RECHARGE(1,"搬运工充值"),PORTER_WITHDROW(2,"搬运工提现"),
        MER_RECHARGE(3,"商户充值"),MER_WITHDROW(4,"商户提现");
        private int value; private String name;
        OrderType(int value,String name){this.value = value;this.name = name;}
    }

}
