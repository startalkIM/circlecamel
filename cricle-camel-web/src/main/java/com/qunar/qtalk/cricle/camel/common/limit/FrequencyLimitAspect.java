package com.qunar.qtalk.cricle.camel.common.limit;

import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class FrequencyLimitAspect {

    public static final String NAME_SPLITTER = "#";



    @Resource
    private RedisUtil redisUtil;

    @Around("@annotation(com.qunar.qtalk.cricle.camel.common.limit.FrequencyLimit)")
    public Object around(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();

        if (targetMethod != null && targetMethod.isAnnotationPresent(FrequencyLimit.class)) {
            FrequencyLimit annotation = targetMethod.getAnnotation(FrequencyLimit.class);
            String name = generateLimitName(pjp,annotation);
            log.info("FrequencyLimit key name:{}", name);
            int time = annotation.time();
            int second = annotation.second();
            if (!RedisAccessor.execute(key -> redisUtil.exists(key), name)) {
                RedisAccessor.execute(key -> redisUtil.setex(key, second, String.valueOf(1)), name);
            } else {
                Long curTime = RedisAccessor.execute(key -> redisUtil.incr(key), name);
                if (curTime > time) {
                    log.warn("method {} limit is reach,curTime:{},configTime:{}", name, curTime, time);
                    return null;
                }
            }
        }

        try {
            return pjp.proceed();
        } catch (Throwable e) {
            log.error("call target method occur exception", e);
            return null;
        }
    }

    private Method extractMethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        return methodSignature.getMethod();
    }

    private String generateLimitName(ProceedingJoinPoint pjp, FrequencyLimit frequencyLimit) {
        String name = frequencyLimit.name();
        if (Objects.equals(name, "")) {
            Object target = pjp.getTarget();
            String clazzName = target.getClass().getSimpleName();
            name = clazzName + NAME_SPLITTER + extractMethod(pjp).getName();
        }
        return name;
    }

}
