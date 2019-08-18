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
import org.joda.time.DateTime;
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
        long sT = System.currentTimeMillis();
        settleDate = StringUtils.isBlank(settleDate) ? DateUtils.getDate(null, -1) : settleDate;
        log.info("[{}]代理收益结算开始，[{}]", settleDate);
        int rNum = 0;
        List<AgentUserEntity> agentUserList = agentUserService.list();
        if (!CollectionUtils.isEmpty(agentUserList)) {
            // 1 接单基础数据查询
            List<AgentSettleUserRecordEntity> agentSettleUserRecordEntities = agentSettleUserRecordService.listBySettleDate(settleDate);
            if (!CollectionUtils.isEmpty(agentSettleUserRecordEntities)) {
//                1 数据初始化
                // userId - agentId
//                1.1 define
                Map<Integer, Integer> userAgentMap = new HashMap<>(agentUserList.size());
                Map<Integer, BigDecimal> agentChargeRateMap = new HashMap<>(agentUserList.size());
                // agent 下的所有代理
//                Map<Integer, Set<Integer>> agentUserSetMap = new HashMap<>(agentUserList.size());
                Map<Integer, AgentSettleDetailEntity> agentSettleDetaiMap = new HashMap<>(agentUserList.size());
                // 避免顶级代理没数据 fixme 容量待定
                Set<Integer> agentSet = new HashSet<>(agentUserList.size() + 1);
//                1.2 init
                agentUserList.forEach(v -> {
                    agentSet.add(v.getUserId());
                    agentSet.add(v.getAgentId());
                    userAgentMap.put(v.getUserId(), v.getAgentId());
                    agentChargeRateMap.put(v.getUserId(), v.getRecvChargeRate());
//                    agentSettleDetaiMap.put(
////                            v.getUserId(),
////                            AgentSettleDetailEntity.builder()
////                                    .agentId(v.getAgentId())
////                                    .createTime(new Date()).settleStatus(1)
////                                    .chargeRate(v.getRecvChargeRate())
////                                    .settleAmount(BigDecimal.ZERO).settleUserNum(0).settleOrderNum(0).settleProfit(BigDecimal.ZERO)
////                                    .build());
////                    3
                 /*   Set<Integer> users = agentUserSetMap.get(v.getAgentId());
                    if (null == users) {
                        users = new HashSet<>();
                    }
                    users.add(v.getUserId());
                    agentUserSetMap.put(v.getAgentId(), users);*/
                });
//                Set<Integer> agentSettleDetaiMapKey = agentSettleDetaiMap.keySet();
//                agentSet.stream()
//                        .filter(v -> agentSettleDetaiMapKey.contains(v))
//                        .forEach(v -> agentSettleDetaiMap.put(v, AgentSettleDetailEntity.builder()
//                                .agentId(v)
//                                .createTime(new Date()).settleStatus(1)
//                                .chargeRate(BigDecimal.ONE) // TODO 顶级代理???
//                                .settleAmount(BigDecimal.ZERO).settleUserNum(0).settleOrderNum(0).settleProfit(BigDecimal.ZERO)
//                                .build()));

                Map<Integer, AgentSettleUserRecordEntity> userBaseReportDataMap = new HashMap<>(agentUserList.size());
                for (AgentSettleUserRecordEntity item : agentSettleUserRecordEntities) {
                    userBaseReportDataMap.put(item.getUserId(), item);
                }
                // 2 代理收益分级计算
                // 2.1
                agentSet.forEach(v -> {
                            // 基础报表数据
                            AgentSettleUserRecordEntity asure = userBaseReportDataMap.get(v);
                            if (null != asure) {// 先查接单人的报表数据， 通过 execAgentSettleProfitHandle 进行代理费计算
                                AgentSettleDetailEntity asde = AgentSettleDetailEntity.builder()
                                        .agentId(v).settleStatus(1)
                                        .settleType(asure.getOrderType()).settleDate(asure.getSettleDate())
                                        .settleAmount(asure.getAmount())
                                        .chargeRate(asure.getChargeRate())
                                        .settleProfit(asure.getAmount().multiply(asure.getChargeRate()).setScale(4))
                                        .settleOrderNum(asure.getNum())
                                        .settleUserNum(1)
                                        .settleRecord("结算过程[userId_amount_chargeRate]:" + v + Constant.SPLIT_CHAR_UNDERLINE + asure.getAmount() + Constant.SPLIT_CHAR_UNDERLINE + asure.getChargeRate())
                                        .createTime(DateTime.now().toDate())
                                        .build();
                                agentSettleDetaiMap.put(v, asde);

                            }
                        }
                );
                // 2.2
                agentSet.forEach(v -> {
                            if (userAgentMap.containsKey(v)) {
                                AgentSettleDetailEntity selfSDE = agentSettleDetaiMap.get(v);
                                execAgentSettleProfitHandle(v, userAgentMap, selfSDE, agentChargeRateMap, agentSettleDetaiMap);
                            }
                        }
                );
//                3 批量保存
                Collection<AgentSettleDetailEntity> agentSettleDetailEntities = agentSettleDetaiMap.values();
                saveBatch(agentSettleDetailEntities);
            } else {
                log.warn("agent_settle_user_record表当日[{}]数据为空，不参与计算!!", settleDate);
            }
        } else {
            log.warn("agent_user数据为null，不参与计算!!");
        }
        log.info("[{}]代理收益结算结束，耗时[{}].", settleDate, System.currentTimeMillis() - sT);
        return rNum;
    }

    /**
     * 代理所得利息计算
     *
     * @param agentId              用户id
     * @param userAgentMap         用户代理map
     * @param agentSettleProfitMap 代理结算费集合
     */
    private void execAgentSettleProfitHandle(Integer agentId,
                                             Map<Integer, Integer> userAgentMap,
                                             AgentSettleDetailEntity selfSDE,
                                             Map<Integer, BigDecimal> agentChargeRateMap,
                                             Map<Integer, AgentSettleDetailEntity> agentSettleProfitMap) {
//        agentSettleProfitMap.put(agentId, selfSDE);
        Integer superiorAgentId = userAgentMap.get(agentId);
        long startTime = System.currentTimeMillis();
        log.info("代理收益计算开始：AgentId[{}] 上级代理[{}].", agentId, superiorAgentId);
        if (null == superiorAgentId) {
            log.warn("Error,{} has nont superiorAgent", agentId);
            return;
//        最近一个代理收益处理
        }
        AgentSettleDetailEntity agentSDE = agentSettleProfitMap.get(superiorAgentId);
        if (null == agentSDE) {
//            String settleRecord = "结算过程:" + userId + Constant.SPLIT_CHAR_COLON + selfSDE.getSettleAmount() + Constant.SPLIT_CHAR_COLON + selfSDE.getChargeRate();
            BigDecimal chargeRate = agentChargeRateMap.get(superiorAgentId);

            agentSDE = AgentSettleDetailEntity.builder()
                    .agentId(superiorAgentId)
                    .chargeRate(null == chargeRate ? BigDecimal.ONE : chargeRate)
                    .settleType(selfSDE.getSettleType())
                    .settleDate(selfSDE.getSettleDate())
                    .settleAmount(selfSDE.getSettleAmount())
                    .settleOrderNum(selfSDE.getSettleOrderNum())
                    .settleUserNum(selfSDE.getSettleUserNum())
                    .settleProfit(selfSDE.getSettleProfit())
                    .createTime(selfSDE.getCreateTime())
                    .settleRecord(selfSDE.getSettleRecord())
                    .settleStatus(1)
                    .build();
            agentSettleProfitMap.put(superiorAgentId, agentSDE);
        } else {
// 代理人数据修改
            agentSDE.setSettleAmount(agentSDE.getSettleAmount().add(selfSDE.getSettleAmount()));
            agentSDE.setSettleOrderNum(agentSDE.getSettleOrderNum() + selfSDE.getSettleOrderNum());
            agentSDE.setSettleUserNum(agentSDE.getSettleUserNum() + selfSDE.getSettleUserNum());
// 代理的费率 - 自己的费率
            BigDecimal diffChargeRate = agentSDE.getChargeRate().subtract(selfSDE.getChargeRate());
            if (diffChargeRate.compareTo(BigDecimal.ZERO) < 0) {
                agentSDE.setRemark(agentSDE.getRemark() + " 费率异常[userId:" + selfSDE.getAgentId() + ",selfChargeRate:" + selfSDE.getChargeRate() + " ,agentChargeRate:" + agentSDE.getChargeRate() + ",diffChargeRate:" + diffChargeRate + "]");
                return;
            } else {
                agentSDE.setSettleProfit(agentSDE.getSettleProfit().add(selfSDE.getSettleAmount().multiply(diffChargeRate)).setScale(4));
                agentSDE.setSettleRecord(agentSDE.getSettleRecord() + " " + selfSDE.getSettleRecord().replace("结算过程[userId_amount_chargeRate]:", " "));
            }
        }
        // 递归最近上级代理
        /*Integer nextSuperiorAgentId = userAgentMap.get(superiorAgentId);
        if (*//*userAgentMap.containsKey(superiorAgentId)*//*null != nextSuperiorAgentId) {
            long tS = System.currentTimeMillis();
            log.info("代理[{}] 有上级代理[{}]，递归计算上级收益开始", superiorAgentId, nextSuperiorAgentId);
            execAgentSettleProfitHandle(superiorAgentId, userAgentMap, agentSDE, agentChargeRateMap, agentSettleProfitMap);
            log.info("代理[{}] 有上级代理[{}]，递归计算上级收益结束,耗时[{}ms].", superiorAgentId, nextSuperiorAgentId, System.currentTimeMillis() - tS);
        }*/
        log.info("代理收益计算结束,AgentId[{}] Superior[{}] 耗时[{}ms].", agentId, superiorAgentId, System.currentTimeMillis() - startTime);
    }
}
