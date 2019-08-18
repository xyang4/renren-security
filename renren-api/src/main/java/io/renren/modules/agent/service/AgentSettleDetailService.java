package io.renren.modules.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.agent.entity.AgentSettleDetailEntity;

/**
 * 代理结算收益记录表
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:25
 */
public interface AgentSettleDetailService extends IService<AgentSettleDetailEntity> {
    /**
     * 代理收益结算
     *
     * @param settleDate
     * @return
     */
    int execProfitSettleReport(String settleDate);
}