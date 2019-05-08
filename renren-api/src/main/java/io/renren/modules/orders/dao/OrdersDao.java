package io.renren.modules.orders.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.orders.entity.OrdersEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
@Mapper
public interface OrdersDao extends BaseMapper<OrdersEntity> {
	
}
