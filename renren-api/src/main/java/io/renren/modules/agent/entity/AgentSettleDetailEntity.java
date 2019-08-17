package io.renren.modules.agent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理结算收益记录表
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:25
 */
@Data
@TableName("agent_settle_detail")
@Builder
public class AgentSettleDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Integer id;
    /**
     * 代理id
     */
    private Integer agentId;
    /**
     * 结算订单类型:1搬运工充值2搬运工提现3商户充值4商户提现
     */
    private Integer settleType;
    /**
     * 结算日期
     */
    private String settleDate;
    /**
     * 代理费率
     */
    private BigDecimal chargeRate;
    /**
     * 结算金额
     */
    private BigDecimal settleAmount;
    /**
     * 结算订单数量
     */
    private Integer settleOrderNum;
    /**
     * 结算下级代理人数
     */
    private Integer settleUserNum;
    /**
     * 结算收益
     */
    private BigDecimal settleProfit;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 计算过程：由userId:amount:charge_rate拼接
     */
    private String settleRecord;
    /**
     * 结算状态
     */
    private Integer settleStatus;

}
