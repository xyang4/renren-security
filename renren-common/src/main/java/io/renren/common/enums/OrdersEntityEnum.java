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
        public static boolean contains(int value) {
            for (OrderType c : OrderType.values()) {
                if (c.value==value) { return true; }
            }
            return false;
        }
    }
    /**
     * 支付类型：wxqr微信二维码 aliqr支付宝二维码 bank银行卡转账 alitr支付宝转账
     */
    @Getter
    public static enum PayType{
        WXQR("wxqr","微信二维码"),ALIQR("aliqr","支付宝二维码"),
        BANK("bank","银行卡转账"),ALITR("alitr","支付宝转账");
        private String value; private String name;
        PayType(String value,String name){this.value = value;this.name = name;}
        public static boolean contains(String value) {
            for (PayType c : PayType.values()) {
                if (c.value.equals(value)) { return true;}
            }
            return false;
        }
    }


}
