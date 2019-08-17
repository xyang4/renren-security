package io.renren.modules.agent.dao;

import io.renren.modules.agent.entity.AgentSettleDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代理结算收益记录表
 * 
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:25
 */
@Mapper
public interface AgentSettleDetailDao extends BaseMapper<AgentSettleDetailEntity> {
	
}
