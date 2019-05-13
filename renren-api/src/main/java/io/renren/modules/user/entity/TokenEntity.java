/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("tb_token")
public class TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(type = IdType.INPUT)
    private Integer userId;
    private String mobile;
    private String token;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
