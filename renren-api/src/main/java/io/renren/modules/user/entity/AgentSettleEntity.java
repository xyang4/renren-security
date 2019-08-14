package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-08-04 00:52:01
 */
@Data
@TableName("agent_settle")
public class AgentSettleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 平台订单号
	 */
	@TableId
	private Integer anentSettleId;
	/**
	 * 订单日期(商户生成的订单的日期,格式YYYY-MM-DD)
	 */
	private String settleDate;
	/**
	 * 订单号
	 */
	private Integer orderId;
	/**
	 * 订单类型（1搬运工充值2搬运工提现3商户充值4商户提现）
	 */
	private Integer orderType;
	/**
	 * 订单状态 0初始1-订单提交 通知抢单,待接单2-已接单3-用户取消4-超时未接单系统取消5-等待打款并确认6-超时未打款取消7-支付受限,重新派单8-发单确认打款9-收单确认已打款 ,订单完成15-等待打款--更换付款方式30-客服处理为取消31-客服处理为完成
	 */
	private Integer orderState;
	/**
	 * 发单用户
	 */
	private Integer sendUserId;
	/**
	 * 接单用户
	 */
	private Integer recvUserId;
	/**
	 * 原始发单金额
	 */
	private BigDecimal sendAmount;
	/**
	 * 平台分配金额
	 */
	private BigDecimal amount;
	/**
	 * 实际收单金额
	 */
	private BigDecimal recvAmount;
	/**
	 * 数据状态：valid有效，invalid无效
	 */
	private String dataStatus;
	/**
	 * 修改时间
	 */
	private String modifyTime;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 发单扣除费率
	 */
	private BigDecimal sendRate;
	/**
	 * 接单奖励费率
	 */
	private BigDecimal recvRate;
	/**
	 * 发单扣除费率金额
	 */
	private BigDecimal sendRateAmount;
	/**
	 * 接单奖励费率金额
	 */
	private BigDecimal recvRateAmount;
	/**
	 * 代理结算收益
	 */
	private BigDecimal settleAmount;

}
