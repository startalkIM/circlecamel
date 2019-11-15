package com.qunar.qtalk.cricle.camel.common.jedis;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class JesdiFactory {

    @Resource
    private JedisConfig jedisConfig;

    @Bean
    public JedisPoolConfig jedisPoolConfigFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(jedisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(jedisConfig.getPoolMaxActive());
        jedisPoolConfig.setMaxWaitMillis(jedisConfig.getPoolMaxWaitMillis());
        jedisPoolConfig.setTestOnBorrow(jedisConfig.getPoolTestOnBorrow().equals("true"));
        jedisPoolConfig.setTestOnReturn(jedisConfig.getPoolTestOnReturn().equals("true"));
        return jedisPoolConfig;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        RedisNode master = new RedisNode(jedisConfig.getSentinelMaster(),0);
        master.setName(jedisConfig.getSentinelMaster());
        Set<RedisNode> sentinels = new HashSet<>();
        sentinels.add(new RedisNode(jedisConfig.getSentinelHost1(), jedisConfig.getSentinelPort()));
        sentinels.add(new RedisNode(jedisConfig.getSentinelHost2(), jedisConfig.getSentinelPort()));
        redisSentinelConfiguration.setMaster(master);
        redisSentinelConfiguration.setSentinels(sentinels);
        return redisSentinelConfiguration;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfigFactory());
        jedisConnectionFactory.setPassword(jedisConfig.getSentinelPass());
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory());
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

}
