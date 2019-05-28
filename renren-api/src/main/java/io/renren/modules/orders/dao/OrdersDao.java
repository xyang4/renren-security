package io.renren.modules.orders.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.orders.entity.OrdersEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
@Mapper
public interface OrdersDao extends BaseMapper<OrdersEntity> {

    List<OrdersEntity> getOrders(Map<String, Object> param);

    List<OrdersEntity> getSendOrRecvOrderList(Page<OrdersEntity> page, Map<String, Object> param);
}
