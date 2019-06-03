package io.renren.modules.orders.service.impl;

import io.renren.modules.orders.dao.OrdersLogDao;
import io.renren.modules.orders.entity.OrdersLogEntity;
import io.renren.modules.orders.service.OrdersLogService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("ordersLogService")
public class OrdersLogServiceImpl extends ServiceImpl<OrdersLogDao, OrdersLogEntity> implements OrdersLogService {



}
