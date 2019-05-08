package io.renren.modules.orders.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.modules.orders.dao.OrdersDao;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("ordersService")
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        return null;
    }

}
