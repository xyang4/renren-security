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
 * @date 2019-05-26 16:49:15
 */
@Data
@TableName("orders")
public class OrdersEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 平台订单号
     */
    @TableId
    private Integer orderId;
    /**
     * 订单日期(商户生成的订单的日期,格式YYYY-MM-DD)
     */
    private String orderDate;
    /**
     * 发单用户
     */
    private Integer sendUserId;
    /**
     * 接单用户
     */
    private Integer recvUserId;
    /**
     * 订单类型（1搬运工充值2搬运工提现3商户充值4商户提现）
     */
    private Integer orderType;
    /**
     * 订单状态 0初始1-订单提交 通知抢单,待接单2-已接单3-用户取消4-超时未接单系统取消5-等待打款并确认6-超时未打款取消7-支付受限,重新派单8-发单确认打款9-收单确认已打款 ,订单完成15-等待打款--更换付款方式30-客服处理为取消31-客服处理为完成
     */
    private Integer orderState;
    /**
     * 支付类型：wxqr微信二维码 aliqr支付宝二维码 bank银行卡转账 alitr支付宝转账
     */
    private String payType;
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
     * 内部订单：商户订单号/其他内部订单
     */
    private String orderSn;
    /**
     * 接单超时时间
     */
    private Integer timeoutRecv;
    /**
     * 打款超时时间
     */
    private Integer timeoutPay;
    /**
     * 订单超时时间
     */
    private Integer timeoutDown;
    /**
     * 是否API： 0-平台  1-api
     */
    private Integer isApi;
    /**
     * 平台日期(平台生成的订单日期,格式YYYY-MM-DD)
     */
    private String platDate;
    /**
     * 客服操作记录
     */
    private Integer serviceRemark;
    /**
     * 发单用户账户编号（银行卡号）
     */
    private String sendAccountNo;
    /**
     * 发单用户账户名称（银行账户户名）
     */
    private String sendAccountName;
    /**
     * 接单用户账户编号（银行卡号）
     */
    private String recvAccountNo;
    /**
     * 接单用户账户名称（银行账户户名）
     */
    private String recvAccountName;
    /**
     * 支付银行名称
     */
    private String bankName;
    /**
     * 二维码图片编号(图片表)
     */
    private Integer qrimgId;
    /**
     * 回调地址
     */
    private String notifyUrl;
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
     * 发单用户银行名称
     */
    private String sendBankName;
    /**
     * 接单用户银行名称
     */
    private String recvBankName;

    /**
     * 发单扣除费率
     */
    private String sendRate;
    /**
     * 接单奖励费率
     */
    private String rrecvRate;
    /**
     * 发单扣除费率金额
     */
    private String sendRateAmount;
    /**
     * 接单奖励费率金额
     */
    private String rrecvRateAmount;

}
