package io.renren.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统字典表
 * </p>
 *
 * @author xYang
 * @since 2019-01-02
 */
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemDict implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @TableField("`key`")
    private String key;
    private String val;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    /**
     * 状态 1 正常 2 禁用 3 已删除
     */
    private Integer status;

}
