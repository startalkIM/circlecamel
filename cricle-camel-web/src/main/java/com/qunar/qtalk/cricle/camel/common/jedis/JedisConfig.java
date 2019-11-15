package com.qunar.qtalk.cricle.camel.common.jedis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = JedisConfig.PREFIX)
public class JedisConfig {
    public static final String PREFIX = "jedis";
    private Integer poolMaxIdle;
    private Integer poolMaxActive;
    private Integer poolMaxWaitMillis;
    private String poolTestOnBorrow;
    private String poolTestOnReturn;
    private String sentinelHost1;
    private String sentinelHost2;
    private String sentinelMaster;
    private Integer sentinelPort;
    private String sentinelPass;
}
