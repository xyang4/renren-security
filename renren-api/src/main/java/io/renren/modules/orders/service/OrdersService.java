package io.renren.modules.orders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.orders.entity.OrdersEntity;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
public interface OrdersService extends IService<OrdersEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

