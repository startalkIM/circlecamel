package com.qunar.qtalk.cricle.camel.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.qunar.qtalk.cricle.camel.common.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Configuration
@EnableConfigurationProperties
public class DataSourceConfig {

    //todo:config dataSoruce pool.

    @Bean("masterDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource masterDataSource() {
        return new DruidDataSource();
    }

    @Bean("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource slaveDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource master,
                                               @Qualifier("slaveDataSource") DataSource slave) {
        Map<String, DataSource> targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSource.MASTER, master);
        targetDataSources.put(DynamicDataSource.SLAVE, slave);
        return new DynamicDataSource(master, targetDataSources);
    }

}
