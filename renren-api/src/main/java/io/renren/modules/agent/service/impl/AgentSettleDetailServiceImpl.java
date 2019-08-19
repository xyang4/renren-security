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
import java.util.stream.Collectors;


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
        log.info("[{}]代理收益结算开始.", settleDate);
        int rNum = 0;
        List<AgentUserEntity> agentUserList = agentUserService.list();
        if (!CollectionUtils.isEmpty(agentUserList)) {
            // 代理基础报表数据查询
            List<Map<String, Object>> agentSettleRecordList = agentSettleUserRecordService.listAgentSettleRecord(settleDate);
            if (!CollectionUtils.isEmpty(agentSettleRecordList)) {
//                1 数据初始化
                // userId - agentId
//                1.1 define
                Map<Integer, Integer> userAgentMap = new HashMap<>(agentUserList.size());
                Map<Integer, BigDecimal> agentChargeRateMap = new HashMap<>(agentUserList.size());
                Map<Integer, AgentSettleDetailEntity> agentSettleDetaiMap = new HashMap<>(agentUserList.size());
                final Set<Integer> agentSet = new HashSet<>();
//                1.2 init
                agentUserList.forEach(v -> {
                    agentSet.add(v.getUserId());
                    agentSet.add(v.getAgentId());
                    userAgentMap.put(v.getUserId(), v.getAgentId());
                    agentChargeRateMap.put(v.getUserId(), v.getRecvChargeRate());
                });
                Map<Integer, Map<String, Object>> userBaseReportDataMap = new HashMap<>(agentUserList.size());
                for (Map<String, Object> item : agentSettleRecordList) {
                    userBaseReportDataMap.put((Integer) item.get("agentId"), item);
                }
                // 2 代理收益处理
                // 2.1 init settle data
                agentSet.forEach(v -> {
                            // 基础报表数据
//                            AgentSettleUserRecordEntity asure = userBaseReportDataMap.get(v);
                            Map<String, Object> tMap = userBaseReportDataMap.get(v);

                            if (null != tMap) {// 初始化最末端代理（有直接接单会员的代理）结算收益，其上级代理在 execAgentSettleProfitHandle 中计算
//                                AGENT_ID agentId,settle_date settleDate,order_type orderType,COUNT(1) userNum,SUM(num) orderNum,SUM(AMOUNT)
                                BigDecimal chargeRate = agentChargeRateMap.get(v);
                                chargeRate = null == chargeRate ? new BigDecimal("0.012") : chargeRate;
                                AgentSettleUserRecordEntity asure = AgentSettleUserRecordEntity.builder()
                                        .agentId((Integer) tMap.get("agentId"))
                                        .settleDate((String) tMap.get("settleDate"))
                                        .orderType((Integer) tMap.get("orderType"))
                                        .num(((BigDecimal) tMap.get("orderNum")).intValue())
                                        .amount((BigDecimal) tMap.get("recvAmount"))
                                        .chargeRate(chargeRate)
                                        .build();
                                AgentSettleDetailEntity asde = AgentSettleDetailEntity.builder()
                                        .agentId(v)
                                        .settleType(asure.getOrderType()).settleDate(asure.getSettleDate())
                                        .settleAmount(asure.getAmount())
                                        .chargeRate(asure.getChargeRate())
                                        // fixme 最末级代理收益已计算，该处不做计算
                                        .settleProfit(/*selfSDE.getSettleProfit()*/BigDecimal.ZERO)
                                        .settleOrderNum(asure.getNum())
                                        .settleUserNum(Integer.valueOf(tMap.get("userNum") + "")) // 下级代理人数
                                        .settleStatus(1)
                                        .settleRecord("结算过程[userId_amount_chargeRate]: " + v + Constant.SPLIT_CHAR_UNDERLINE + asure.getAmount() + Constant.SPLIT_CHAR_UNDERLINE + asure.getChargeRate())
                                        .createTime(DateTime.now().toDate())
                                        .build();
                                agentSettleDetaiMap.put(v, asde);
                            }
                        }
                );

                // 代理排序，根据上级代理数倒序排，便于逐级计算代理费
                Map<Integer, Integer> agentDepth = new HashMap<>(agentSet.size());
                for (Integer agentId : agentSet) {
                    Integer num = agentDepth.get(agentId);
                    if (null == num) num = 0;
                    if (userAgentMap.containsKey(agentId)) {
                        num = execAgentCount(agentId, num, userAgentMap);
                    }
                    agentDepth.put(agentId, num);
                }
                List<Integer> sortedAgentList = agentDepth.entrySet().stream()
                        .sorted((Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) -> o2.getValue() - o1.getValue())
                        .map(entry -> entry.getKey())
                        .collect(Collectors.toList());

                // 2.2 exec settle handle
                sortedAgentList.forEach(v -> {
                            if (userAgentMap.containsKey(v)) {
                                AgentSettleDetailEntity selfSDE = agentSettleDetaiMap.get(v);
                                if (null != selfSDE) { //  末端人员不在 agentSettleDetaiMap 里，无直接有接单用户的代理也不在 agentSettleDetaiMap 里，通过 execAgentSettleProfitHandle 计算结算收益
                                    execAgentSettleProfitHandle(v, userAgentMap, selfSDE, agentChargeRateMap, agentSettleDetaiMap);
                                }
                            }
                        }
                );
//                3 批量保存
                Collection<AgentSettleDetailEntity> agentSettleDetailEntities = agentSettleDetaiMap.values();
                saveBatch(agentSettleDetailEntities, 20);
//                saveBatch(agentSettleDetailEntities);
                rNum = agentSettleDetailEntities.size();
            } else {
                log.warn("agent_settle_user_record表当日[{}]数据为空，不参与计算!!", settleDate);
            }
        } else {
            log.warn("agent_user数据为null，不参与计算!!");
        }
        log.info("[{}]代理收益结算结束，保存成功[{}]条，耗时[{}].", settleDate, rNum, System.currentTimeMillis() - sT);
        return rNum;
    }

    private int execAgentCount(Integer agentId, Integer num, Map<Integer, Integer> userAgentMap) {
        Integer sa = userAgentMap.get(agentId);
        if (null != sa) {
            num++;
            num = execAgentCount(sa, num, userAgentMap);
        }
        return num;
    }

    /**
     * 代理结算收益计算
     *
     * @param agentId              用户id
     * @param userAgentMap         用户代理map
     * @param agentChargeRateMap   代理可接受费率（前提是一个代理只有一个上级代理!!!）
     * @param agentSettleProfitMap 代理结算费集合
     */
    private void execAgentSettleProfitHandle(Integer agentId,
                                             Map<Integer, Integer> userAgentMap,
                                             AgentSettleDetailEntity selfSDE,
                                             Map<Integer, BigDecimal> agentChargeRateMap,
                                             Map<Integer, AgentSettleDetailEntity> agentSettleProfitMap) {
        Integer superiorAgentId = userAgentMap.get(agentId);
        long startTime = System.currentTimeMillis();
        log.info("代理收益计算开始：AgentId[{}] 上级代理[{}].", agentId, superiorAgentId);
        if (null == superiorAgentId) {
            log.warn("Error,{} has nont superiorAgent", agentId);
            return;
        }

        AgentSettleDetailEntity agentSDE = agentSettleProfitMap.get(superiorAgentId);
        BigDecimal agentChargeRate = agentChargeRateMap.get(superiorAgentId);
        agentChargeRate = null == agentChargeRate ? new BigDecimal("0.012") : agentChargeRate;
        // 代理及上级代理的费率差
        BigDecimal diffChargeRate = agentChargeRate.subtract(selfSDE.getChargeRate());
        if (diffChargeRate.compareTo(BigDecimal.ZERO) < 0) {
            log.error("代理费率异常[userId:" + selfSDE.getAgentId() + ",selfChargeRate:" + selfSDE.getChargeRate() + " ,agentChargeRate:" + agentSDE.getChargeRate() + ",diffChargeRate:" + diffChargeRate + "]");
            return;
        }
        if (null == agentSDE) { // 上级代理不存在，初始化（复制自己的基础数据,收益须单独计算!!!）
            agentSDE = AgentSettleDetailEntity.builder()
                    .agentId(superiorAgentId)
                    .chargeRate(agentChargeRate)
                    .settleType(selfSDE.getSettleType())
                    .settleDate(selfSDE.getSettleDate())
                    .settleAmount(selfSDE.getSettleAmount())
                    .settleOrderNum(selfSDE.getSettleOrderNum())
                    .settleUserNum(selfSDE.getSettleUserNum())
                    .settleProfit(selfSDE.getSettleAmount().multiply(diffChargeRate)) // 收益计算
                    .createTime(selfSDE.getCreateTime())
                    .settleRecord(selfSDE.getSettleRecord())
                    .settleStatus(1)
                    .build();
            agentSettleProfitMap.put(superiorAgentId, agentSDE);
        } else { // 上级代理存在，更新上级代理数据
            agentSDE.setSettleAmount(agentSDE.getSettleAmount().add(selfSDE.getSettleAmount()));
            agentSDE.setSettleOrderNum(agentSDE.getSettleOrderNum() + selfSDE.getSettleOrderNum());
            agentSDE.setSettleUserNum(agentSDE.getSettleUserNum() + selfSDE.getSettleUserNum());
            agentSDE.setSettleProfit(agentSDE.getSettleProfit().add(selfSDE.getSettleAmount().multiply(diffChargeRate)).setScale(4));
            // fixme 结算过程须优化: A>a>a1>a11
            agentSDE.setSettleRecord(agentSDE.getSettleRecord() + "|" + selfSDE.getSettleRecord().replace("结算过程[userId_amount_chargeRate]: ", ""));
        }
        log.info("代理收益计算结束,AgentId[{}] Superior[{}] 耗时[{}ms].", agentId, superiorAgentId, System.currentTimeMillis() - startTime);
    }
}
