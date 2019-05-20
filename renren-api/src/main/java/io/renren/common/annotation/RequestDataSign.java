package io.renren.common.annotation;

import java.lang.annotation.*;

/**
 * 加该注解，需要进行数据签名校验
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestDataSign {
}
