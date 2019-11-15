package com.qunar.qtalk.cricle.camel.jedisTest;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import org.junit.Test;

import javax.annotation.Resource;

public class JedisTest extends BaseTest {
    @Resource
    private RedisUtil redisUtil;

    @Test
    public void jedisTest() {
        redisUtil.set(2, "testBin", "12");
        System.out.println(redisUtil.get(2, "testBin", String.class));
    }
}
