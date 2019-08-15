package io.renren.modules.common.controller.form;

import io.renren.common.domain.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("验证码发送实体类")
public class SmsSubmitForm extends BaseForm {
    @NotNull
    private String mobile;
    @ApiModelProperty("短信类型:1 短信 2 其他")
    private Integer type;
    @ApiModelProperty("图形验证码")
    @NotNull
    private String kaptcha;
}
