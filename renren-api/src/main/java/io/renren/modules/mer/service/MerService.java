package io.renren.modules.mer.service;


import io.renren.modules.orders.entity.OrdersEntity;

public interface MerService {

    /**
     * 校验商户有效性
     */
    public boolean checkMer(Integer merId);

    /**
     * 异步通知商户订单结果
     */
    public boolean sendOrderNotify(OrdersEntity ordersEntity);

}
