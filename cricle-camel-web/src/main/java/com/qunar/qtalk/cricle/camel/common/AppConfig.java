package com.qunar.qtalk.cricle.camel.common;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.qunar.qtalk.cricle.camel.web.interceptor.CustomInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    private List<HandlerInterceptor> handlerInterceptors;

    public AppConfig(List<HandlerInterceptor> handlerInterceptors) {
        this.handlerInterceptors = handlerInterceptors;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (CollectionUtils.isNotEmpty(handlerInterceptors)) {
            handlerInterceptors.sort(Comparator.comparingInt(this::getOrder));
            handlerInterceptors.forEach(handlerInterceptor -> {
                registry.addInterceptor(handlerInterceptor)
                        .addPathPatterns(getAddPathPatterns(handlerInterceptor))
                        .excludePathPatterns(getExcludePathPatterns(handlerInterceptor));
            });
        }
        super.addInterceptors(registry);
    }

    private int getOrder(HandlerInterceptor handlerInterceptor) {
        CustomInterceptor annotation = AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), CustomInterceptor.class);
        if (annotation == null) {
            return Ordered.LOWEST_PRECEDENCE;
        }
        return annotation.order();
    }

    private String[] getAddPathPatterns(HandlerInterceptor handlerInterceptor) {
        CustomInterceptor annotation = AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), CustomInterceptor.class);
        if (annotation == null) {
            return null;
        }
        return annotation.addPathPatterns();
    }

    private String[] getExcludePathPatterns(HandlerInterceptor handlerInterceptor) {
        CustomInterceptor annotation = AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), CustomInterceptor.class);
        if (annotation == null) {
            return null;
        }
        return annotation.excludePathPatterns();
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        //设置控制台管理用户
        reg.addInitParameter("loginUsername","root");
        reg.addInitParameter("loginPassword","root");
        // 禁用HTML页面上的“Reset All”功能
        reg.addInitParameter("resetEnable","false");
        //reg.addInitParameter("allow", "127.0.0.1"); //白名单
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        //创建过滤器
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        Map<String, String> initParams = new HashMap<String, String>();
        //忽略过滤的形式
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.setInitParameters(initParams);
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

}
