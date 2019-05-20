package io.renren.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 短信发送记录表
 * </p>
 *
 * @author xYang
 * @since 2019-01-03
 */
@Data
@TableName("user_sms_record")
public class UserSmsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 发送者手机号
     */
    private String mobile;
    /**
     * 请求IP
     */
    @TableField("client_ip")
    private String clientIp;
    /**
     * 业务类型: 1 注册 2找回密码 3登录 4其他
     */
    private Integer type;
    /**
     * 验证码
     */
    private String code;
    /**
     * 备注,e.g.失败原因
     */
    private String remark;
    @TableField("create_time")
    private Date createTime;
    /**
     * 1:成功 2:失败
     */
    private Integer status;


    public UserSmsRecord(String mobile, String ip, Integer type, String code) {
        this.mobile = mobile;
        this.clientIp = ip;
        this.type = type;
        this.code = code;
    }

}
