package io.renren.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.user.entity.AgentUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-19 17:13:09
 */
@Mapper
public interface AgentUserDao extends BaseMapper<AgentUserEntity> {

    List<Map<String, Object>> agentUserList(@Param("params") Map<String, Object> param);
    //修改
    int agentUserEdit(Map<String, Object> param);
}
