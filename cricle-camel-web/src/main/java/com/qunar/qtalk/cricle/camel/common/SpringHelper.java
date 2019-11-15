package com.qunar.qtalk.cricle.camel.common;

import com.qunar.qtalk.cricle.camel.common.holder.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by haoling.wang on 2018/9/20.
 */
@Component
@Slf4j
public class SpringHelper implements ApplicationContextAware {

    private static final Holder<ApplicationContext> holder = new Holder<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("init SpringHelper");
        holder.set(applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return holder.get();
    }

    public static Object getBean(String className) throws BeansException {
        return getApplicationContext().getBean(className);
    }

    public static <T> T getBean(Class<T> classType) {
        return getApplicationContext().getBean(classType);
    }
}
