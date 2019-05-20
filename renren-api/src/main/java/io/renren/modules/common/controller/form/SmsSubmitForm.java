package io.renren.modules.common.controller.form;

import lombok.Data;

@Data
public class SmsSubmitForm {
    private String mobile;
    private Integer type;
}
