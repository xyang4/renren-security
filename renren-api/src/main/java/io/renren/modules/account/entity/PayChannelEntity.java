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
 * @date 2019-05-26 13:48:36
 */
@Data
@TableName("pay_channel")
public class PayChannelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 付款通道编号
	 */
	@TableId
	private Integer payChannelId;
	/**
	 * 用户编号
	 */
	private Integer userId;
	/**
	 * 支付类型：wxqr微信二维码 aliqr支付宝二维码 bank银行卡转账 alitr支付宝转账
	 */
	private String payType;
	/**
	 * 账户编号（银行卡号）
	 */
	private String accountNo;
	/**
	 * 账户名称（银行账户户名）
	 */
	private String accountName;
	/**
	 * 账户UID（支付宝uid）
	 */
	private String accountUid;
	/**
	 * 支付银行名称
	 */
	private String bankName;
	/**
	 * 二维码图片编号(图片表)
	 */
	private Integer qrimgId;
	/**
	 * 启用状态 0-停用 1-启用
	 */
	private Integer useStatus;
	/**
	 * 绑定状态：0-解绑 1-绑定
	 */
	private Integer bindStatus;
	/**
	 * 管理状态：0-不可用 1-可用
	 */
	private Integer manageStatus;
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
