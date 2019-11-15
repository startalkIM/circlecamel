package com.qunar.qtalk.cricle.camel.common.config;

import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@Configuration
@ConditionalOnClass({DozerBeanMapperFactoryBean.class, DozerBeanMapper.class})
public class DozerConfig {

    private static final String DEFAULT_DOZER_MAPPER_FILES = "classpath*:dozer/**/*.xml";

    private final ApplicationContext applicationContext;

    DozerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public Mapper mapper() throws Exception {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        factoryBean.setApplicationContext(applicationContext);
        Resource[] resources = applicationContext.getResources(DEFAULT_DOZER_MAPPER_FILES);
        factoryBean.setMappingFiles(resources);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public DozerUtils dozerMapper(Mapper mapper) {
        return new DozerUtils(mapper);
    }
}
