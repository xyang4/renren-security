package io.renren.modules.agent.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 代理每日收益记录
 * 
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:24
 */
@Data
@TableName("agent_settle_user_record")
public class AgentSettleUserRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 代理
	 */
	private Integer userId;
	/**
	 * 订单类型:1搬运工充值2搬运工提现3商户充值4商户提现
	 */
	private Integer orderType;
	/**
	 * 结算日期
	 */
	private String settleDate;
	/**
	 * 收益率
	 */
	private BigDecimal chargeRate;
	/**
	 * 接单数量
	 */
	private Integer num;
	/**
	 * 接单金额
	 */
	private BigDecimal amount;
	/**
	 * 收益
	 */
	private BigDecimal profit;
	/**
	 * 上级代理id
	 */
	private Integer agentId;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
