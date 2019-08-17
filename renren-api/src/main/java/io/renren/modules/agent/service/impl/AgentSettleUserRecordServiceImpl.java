package io.renren.modules.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.DateUtils;
import io.renren.modules.agent.dao.AgentSettleUserRecordDao;
import io.renren.modules.agent.entity.AgentSettleUserRecordEntity;
import io.renren.modules.agent.service.AgentSettleUserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("agentSettleUserRecordService")
@Slf4j
public class AgentSettleUserRecordServiceImpl extends ServiceImpl<AgentSettleUserRecordDao, AgentSettleUserRecordEntity> implements AgentSettleUserRecordService {

    @Autowired
    AgentSettleUserRecordDao dao;

    @Override
    public int execUserRecvReport(String settleDate) {
        // fixme 时间处理
        settleDate = StringUtils.isBlank(settleDate) ? DateUtils.getDate(null, -1) : settleDate;
        log.info("用户接单统计开始，settleDate[{}]", settleDate);
        int i = dao.execUserRecvReport(settleDate);
        log.info("用户接单统计结束，settleDate[{}] R[{}].", settleDate, i);
        return i;
    }

    @Override
    public List<AgentSettleUserRecordEntity> listBySettleDate(String settleDate) {
        Wrapper<AgentSettleUserRecordEntity> wrapper = new QueryWrapper<AgentSettleUserRecordEntity>()
                .eq("settle_date", settleDate);
        return list(wrapper);
    }

}
