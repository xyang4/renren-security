package io.renren.common.aspect;

import io.renren.common.config.RenrenProperties;
import io.renren.common.domain.BaseForm;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Aspect
@Component
@Slf4j
public class RequestParamCheckAop {
    @Autowired
    RenrenProperties renrenProperties;

    @Before("execution(public io.renren.common.utils.R *(..)) && within(io.renren.modules.*.controller..*)")
    public void paramsCheck(JoinPoint joinPoint) {
        for (Object obj : joinPoint.getArgs()) {
            if (renrenProperties.getCheckAndRecordRequestParams() && (obj instanceof BaseForm || obj instanceof Model)) {
                log.info("Request Params[{}].", obj.toString());
            }
            if (obj instanceof BindingResult) {
                BindingResult br = (BindingResult) obj;
                if (br.hasErrors()) {
                    List<FieldError> fieldErrors = br.getFieldErrors();
                    StringBuilder sb = new StringBuilder(fieldErrors.size());
                    for (FieldError err : fieldErrors) {
                        sb.append(err.getField() + ":" + err.getDefaultMessage() + ",");
                    }
                    String msgTip = sb.toString();
                    throw new RRException(RRExceptionEnum.BAD_REQUEST_PARAMS, msgTip.substring(0, msgTip.length() - 1));
                }
            }
        }
    }
}
