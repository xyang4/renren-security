package io.renren.modules.orders.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel(value = "搬运工充值提现表单")
public class HamalOrderForm {
    @ApiModelProperty("金额")
    @NotBlank(message = "金额不能为空")
    private String amount;
    @ApiModelProperty("短信验证码")
    @NotBlank(message = "验证码不能为空")
    private String smsCode;
    @ApiModelProperty("账户名称")
    private String accountName;
    @ApiModelProperty("账号")
    private String accountNo;
    @ApiModelProperty("银行名称")
    private String bankName;
}
