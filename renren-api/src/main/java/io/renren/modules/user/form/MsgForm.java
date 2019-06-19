package io.renren.modules.user.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MsgForm {

    @NotBlank(message = "接收人不能为空")
    private Integer recvUserId;

    @NotBlank(message = "消息类型不能为空")
    private Integer msgType;

    @NotBlank(message = "消息内容不能为空")
    private String charText;
}
