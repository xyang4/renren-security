package io.renren.modules.orders.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.orders.entity.OrdersEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
@Mapper
public interface OrdersDao extends BaseMapper<OrdersEntity> {

    List<Map> getOrders(Map<String, Object> param);

    List<OrdersEntity> getSendOrRecvOrderList(@Param("params") Map<String, Object> param, Page<OrdersEntity> page);

    int reciveOrderSuccess(OrdersEntity updateOrder);

    /**
     * @param typeList
     * @param excludeStatusList
     * @param limit
     * @return
     */
    List<Map<String, Object>> listValidOrders(@Param("typeList") List<Integer> typeList, @Param("stateList") List<Integer> stateList, @Param("excludeStatusList") List<Integer> excludeStatusList, @Param("limit") int limit);

    /**
     * 批量修改订单状态
     *
     * @param idList
     * @param state
     * @param validStateList
     * @return
     */
    int batchUpdateState(@Param("idList") List<Integer> idList, @Param("state") int state, @Param("validStateList") List<Integer> validStateList);
}
