package com.qunar.qtalk.cricle.camel.web.interceptor;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by haoling.wang on 2018/12/29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CustomInterceptor {

    int order() default Ordered.LOWEST_PRECEDENCE;

    String[] addPathPatterns() default "";

    String[] excludePathPatterns() default "";
}
