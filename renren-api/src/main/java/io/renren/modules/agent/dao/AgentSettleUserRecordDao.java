package io.renren.modules.agent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.agent.entity.AgentSettleUserRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 代理每日收益记录
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:24
 */
@Mapper
public interface AgentSettleUserRecordDao extends BaseMapper<AgentSettleUserRecordEntity> {
    /**
     * 用户接单报表统计
     *
     * @param settleDate
     * @return
     */
    int execUserRecvReport(String settleDate);
}
