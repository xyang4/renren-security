package io.renren.modules.account.entity;

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
 * @date 2019-05-13 18:39:26
 */
@Data
@TableName("account_log")
public class AccountLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	@TableId
	private Integer accountLogId;
	/**
	 * 用户编号
	 */
	private Integer userId;
	/**
	 * 订单号
	 */
	private Integer orderId;
	/**
	 * 订单类型（1搬运工充值2搬运工提现3商户充值4商户提现）
	 */
	private Integer orderType;
	/**
	 * 状态：0无效1冻结2解冻3付出4收入5收益6费用7提现8提现费用9提现取消返回10后台入金11后台提现12返利收入13代理提现14夜晚奖励15代理奖励16代理佣金17激活账号扣款18激活奖励19提现未打款扣款
	 */
	private Integer flowType;
	/**
	 * 资金流向：in 进入 out出去
	 */
	private String flow;
	/**
	 * 操作金额
	 */
	private BigDecimal amount;
	/**
	 * 余额
	 */
	private BigDecimal balance;
	/**
	 * 可用金额
	 */
	private BigDecimal canuseAmount;
	/**
	 * 冻结中金额
	 */
	private BigDecimal frozenAmount;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 修改人
	 */
	private String modifyUser;
	/**
	 * 修改时间
	 */
	private String modifyTime;
	/**
	 * 创建人
	 */
	private String createUser;
	/**
	 * 创建时间
	 */
	private String createTime;

}
