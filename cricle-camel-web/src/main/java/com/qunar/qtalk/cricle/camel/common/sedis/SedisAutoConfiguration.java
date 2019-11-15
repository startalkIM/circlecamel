//package com.qunar.qtalk.cricle.camel.common.sedis;
//
//import com.qunar.redis.storage.Sedis3;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Created by haoling.wang on 2018/12/29.
// */
//@Configuration
//@EnableConfigurationProperties({SedisProperties.class})
//@ConditionalOnClass({Sedis3.class})
//public class SedisAutoConfiguration {
//
//    private final SedisProperties sedisProperties;
//
//    public SedisAutoConfiguration(SedisProperties sedisProperties) {
//        this.sedisProperties = sedisProperties;
//    }
//
//    @Bean
//    @ConditionalOnProperty(prefix = SedisProperties.PREFIX, name = {"namespace", "cipher", "zookeeper-address"})
//    @ConditionalOnMissingBean
//    protected Sedis3 sedis3() {
//        return new Sedis3(sedisProperties.getNamespace(), sedisProperties.getCipher(), sedisProperties.getSocketTimeout(),
//                sedisProperties.getPoolCoreSize(), sedisProperties.getPoolMaxSize(), sedisProperties.getZookeeperAddress());
//    }
//}
