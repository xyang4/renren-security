package io.renren.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.user.entity.AgentSettleEntity;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-08-03 20:50:12
 */
public interface AgentSettleService extends IService<AgentSettleEntity> {
    /**
     * 代理商每日结算
     */
    void agentSettle();
}

