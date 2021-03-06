package io.renren.modules.account.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "更新支付渠道表单")
public class UpdatePayChannelFrom {
    @ApiModelProperty("启用状态")
    private String useStatus;
    @ApiModelProperty("绑定状态")
    private String bindStatus;
    @ApiModelProperty("payChannelId")
    @NotBlank(message = "payChannelId不能为空")
    private String payChannelId;
    @ApiModelProperty("图片")
    private String baseImg;
    @ApiModelProperty(value = "账户编号（银行卡号）")
    private String accountNo;
    @ApiModelProperty(value = "账户名称")
    private String accountName;
}
