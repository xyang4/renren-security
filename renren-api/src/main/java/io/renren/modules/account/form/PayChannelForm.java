package io.renren.modules.account.form;

import io.renren.common.domain.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "支付渠道表单")
public class PayChannelForm extends BaseForm {
    @ApiModelProperty(value = "支付类型")
    @NotBlank(message = "支付类型不能为空")
    private String payType;
    @ApiModelProperty(value = "账户编号（银行卡号）")
    private String accountNo;
    @ApiModelProperty(value = "账户名称")
    private String accountName;
    @ApiModelProperty("账户UID（支付宝uid）")
    private String accountUid;
    @ApiModelProperty("图片")
    private String baseImg;
    @ApiModelProperty("银行名称")
    private String bankName;
    @ApiModelProperty("启用状态")
    private int useStatus;
    @ApiModelProperty("绑定状态")
    private int bindStatus;
}
