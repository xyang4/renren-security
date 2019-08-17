package io.renren.modules.agent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.Constant;
import io.renren.common.utils.DateUtils;
import io.renren.modules.agent.dao.AgentSettleDetailDao;
import io.renren.modules.agent.entity.AgentSettleDetailEntity;
import io.renren.modules.agent.entity.AgentSettleUserRecordEntity;
import io.renren.modules.agent.service.AgentSettleDetailService;
import io.renren.modules.agent.service.AgentSettleUserRecordService;
import io.renren.modules.user.entity.AgentUserEntity;
import io.renren.modules.user.service.AgentUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;


@Service("agentSettleDetailService")
@Slf4j
public class AgentSettleDetailServiceImpl extends ServiceImpl<AgentSettleDetailDao, AgentSettleDetailEntity> implements AgentSettleDetailService {
    @Autowired
    AgentUserService agentUserService;
    @Autowired
    AgentSettleUserRecordService agentSettleUserRecordService;

    @Override
    public int execProfitSettleReport(String settleDate) {
        settleDate = StringUtils.isBlank(settleDate) ? DateUtils.getDate(null, -1) : settleDate;
        log.info("代理收益结算开始，settleDate[{}]", settleDate);
        int rNum = 0;
        List<AgentUserEntity> agentUserList = agentUserService.list();
        if (!CollectionUtils.isEmpty(agentUserList)) {

            List<AgentSettleUserRecordEntity> agentSettleUserRecordEntities = agentSettleUserRecordService.listBySettleDate(settleDate);
            if (!CollectionUtils.isEmpty(agentSettleUserRecordEntities)) {
                // 1 根据日期查询所有代理的收益
                Map<Integer, AgentUserEntity> tmpMap = new HashMap<>(agentUserList.size());
                Map<Integer, AgentSettleDetailEntity> agentSettleDetailEntityMap = new HashMap<>(agentUserList.size());
                agentUserList.forEach(v -> {
                    tmpMap.put(v.getAgentId(), v);
                    agentSettleDetailEntityMap.put(
                            v.getAgentId(),
                            AgentSettleDetailEntity.builder()
                                    .agentId(v.getAgentId()).chargeRate(v.getRecvChargeRate()).createTime(new Date()).settleStatus(1)
                                    .settleAmount(BigDecimal.ZERO).settleUserNum(0).settleOrderNum(0).settleProfit(BigDecimal.ZERO)
                                    .build());
                });

                for (AgentSettleUserRecordEntity item : agentSettleUserRecordEntities) {
                    // 代理人数据修改
                    AgentSettleDetailEntity entity = agentSettleDetailEntityMap.get(item.getAgentId());
                    entity.setSettleType(item.getOrderType());
                    entity.setSettleDate(item.getSettleDate());
                    entity.setSettleAmount(entity.getSettleAmount().add(item.getAmount()));
                    entity.setSettleOrderNum(entity.getSettleOrderNum() + item.getNum());
                    entity.setSettleUserNum(entity.getSettleUserNum() + 1);
                    // 代理的费率 - 自己的费率
                    entity.setSettleProfit(entity.getSettleProfit().add(item.getAmount().multiply(entity.getChargeRate().subtract(item.getChargeRate()))));
                    entity.setSettleRecord((null == entity.getSettleRecord() ? null : entity.getSettleRecord() + " ") + item.getUserId() + Constant.SPLIT_CHAR_COLON + item.getAmount() + Constant.SPLIT_CHAR_COLON + item.getChargeRate());
                }

                // 2 代理收益分级计算
                Collection<AgentSettleDetailEntity> agentSettleDetailEntities = agentSettleDetailEntityMap.values();
                saveBatch(agentSettleDetailEntities);
            } else {
                log.warn("agent_settle_user_record表当日[{}]数据为空，不参与计算!!", settleDate);
            }
        } else {
            log.warn("agent_user数据为null，不参与计算!!");
        }
        log.info("代理收益结算结束，settleDate[{}]", settleDate);
        return rNum;
    }
}
