package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-19 17:13:09
 */
@Data
@TableName("agent_user")
public class AgentUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer agentUserId;
	/**
	 * 代理商userid编号
	 */
	private Integer agentId;
	/**
	 * 用户编号
	 */
	private Integer userId;
	/**
	 * 修改时间
	 */
	private String modifyTime;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 接单充值收益费率
	 */
	private BigDecimal recvChargeRate;
}
