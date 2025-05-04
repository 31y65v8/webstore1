package com.wxl.webstore.log.operationlog.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogAnnoce {
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作内容
     */
    String operation() default "";
}
