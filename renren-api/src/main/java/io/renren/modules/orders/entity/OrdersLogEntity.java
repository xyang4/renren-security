package io.renren.modules.orders.entity;

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
 * @date 2019-06-03 14:31:29
 */
@Data
@TableName("orders_log")
public class OrdersLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单日志编号
	 */
	@TableId
	private Integer orderLogId;
	/**
	 * 订单编号
	 */
	private Integer orderId;
	/**
	 * 订单状态 0初始1-订单提交 通知抢单,待接单2-已接单3-用户取消4-超时未接单系统取消5-等待打款并确认6-超时未打款取消7-支付受限,重新派单8-发单确认打款9-收单确认已打款 ,订单完成15-等待打款--更换付款方式30-客服处理为取消31-客服处理为完成
	 */
	private Integer orderState;
	/**
	 * 支付类型：wxqr微信二维码 aliqr支付宝二维码 bank银行卡转账 alitr支付宝转账
	 */
	private String payType;
	/**
	 * 交易金额
	 */
	private BigDecimal amount;
	/**
	 * 收单金额
	 */
	private BigDecimal recvAmount;
	/**
	 * 客服操作记录
	 */
	private Integer serviceRemark;
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
	private String qrimgId;
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

}
