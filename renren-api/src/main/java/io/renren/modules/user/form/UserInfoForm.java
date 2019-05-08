/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * 用户信息表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "用户信息表单")
public class UserInfoForm {

    @ApiModelProperty(value = "手机号")
    @Length(max = 12)
    private String mobile;
    @ApiModelProperty(value = "姓名")
    @Length(max = 10)
    private String name;
    @ApiModelProperty(value = "密码")
    private String password;

}
