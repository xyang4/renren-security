package io.renren.modules.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.agent.entity.AgentSettleUserRecordEntity;

import java.util.List;
import java.util.Map;

/**
 * 代理每日收益记录
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:24
 */
public interface AgentSettleUserRecordService extends IService<AgentSettleUserRecordEntity> {

    /**
     * 用户接单报表统计
     *
     * @param settleDate
     * @return
     */
    int execUserRecvReport(String settleDate);

    /**
     * @param settleDate
     * @return
     */
    List<AgentSettleUserRecordEntity> listBySettleDate(String settleDate);


    List<Map<String,Object>> listAgentSettleRecord(String settleDate);
}

