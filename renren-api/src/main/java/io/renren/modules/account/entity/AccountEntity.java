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
 * @date 2019-05-13 18:22:50
 */
@Data
@TableName("account")
public class AccountEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 账户编号
	 */
	@TableId
	private Integer accountId;
	/**
	 * 用户编号
	 */
	private Integer userId;
	/**
	 * 余额(即总金额=可用金额+冻结金额)
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
	 * 状态：-1-冻结 0-停用1启用
	 */
	private Integer status;
	/**
	 * 发单状态:0-发单1-正常发单
	 */
	private Integer sendStatus;
	/**
	 * 接单状态:0-禁止接单1-正常接单2-禁止接单1天 3-禁止接单2小时
	 */
	private Integer recvStatus;
	/**
	 * 激活状态:0-未激活1-激活
	 */
	private Integer activeStatus;
	/**
	 * 账户类型：common普通账户，plat平台账户
	 */
	private String accountType;
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
	/**
	 * 校验
	 */
	private String sign;

}
