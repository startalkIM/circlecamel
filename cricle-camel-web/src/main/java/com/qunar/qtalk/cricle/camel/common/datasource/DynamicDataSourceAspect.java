package com.qunar.qtalk.cricle.camel.common.datasource;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Around("@annotation(com.qunar.qtalk.cricle.camel.common.datasource.TargetDataSource)")
    public Object around(ProceedingJoinPoint pjp) {
        String targetDs = DynamicDataSource.MASTER;
        try {
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Method targetMethod = methodSignature.getMethod();

            if (targetMethod != null && targetMethod.isAnnotationPresent(TargetDataSource.class)) {
                String targetDataSource = targetMethod.getAnnotation(TargetDataSource.class).value();
                targetDs = targetDataSource;
                log.info("method {} set targetDatasource is {}", targetMethod.getName(), targetDs);
            }
        } catch (Exception e) {
            log.error("get DynamicDataSource occur exception", e);
        }
        DynamicDataSource.setDataSource(targetDs);
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            log.error("do biz occur exception", e);
            throw new RuntimeException(e);
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }
}
