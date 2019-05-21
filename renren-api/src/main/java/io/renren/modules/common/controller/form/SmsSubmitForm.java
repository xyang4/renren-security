package io.renren.modules.common.controller.form;

import io.renren.common.domain.BaseForm;
import lombok.Data;

@Data
public class SmsSubmitForm extends BaseForm {
    private String mobile;
    private Integer type;
}
