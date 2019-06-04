package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-11 18:14:40
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	@TableId
	private Integer userId;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 用户昵称
	 */
	private String nickName;
	/**
	 * 用户类型：porter搬运工、mer商户、agent1级代理商、agent2级代理商、agent3级代理商、clerk接单员
	 */
	private String userType;
	/**
	 * 状态：-1停用 0-待审核 1-审核通过，启用 10-黑户 11-异常
	 */
	private Integer status;
	/**
	 * 用户分组：0-黑名单 1-普通用户，2-白名单，3-平台内部用户
	 */
	private Integer userGroup;
	/**
	 * 用户级别：1级普通, 2级日交易额活跃 ,5万3级10万
	 */
	private Integer userLevel;
	/**
	 * 登录密码
	 */
	private String passwd;
	/**
	 * 支付密码
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String payPasswd;
	/**
	 * 身份证号
	 */
	private String idCard;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 邮箱编号
	 */
	private String email;
	/**
	 * 手机号国家代码
	 */
	private String ccode;
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

	//商户签名key
	private String singKey;

}
