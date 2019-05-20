package io.renren.modules.orders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.orders.entity.OrdersEntity;

import java.util.Map;

/**
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
public interface OrdersService extends IService<OrdersEntity> {
    /**
     * 订单申请预处理
     */
    public Map applyOrder(Integer merId, String orderDate, int orderType, String orderSn,
                          String payType, String sendAmount, String notifyUrl);

    /**
     * 订单有效性校验
     *
     * @param orderInfo
     * @return
     */
    boolean checkValidity(Object orderInfo);
}

