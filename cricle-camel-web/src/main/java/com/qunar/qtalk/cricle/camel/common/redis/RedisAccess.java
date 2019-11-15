package com.qunar.qtalk.cricle.camel.common.redis;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@FunctionalInterface
public interface RedisAccess<T,R> {

    R execute(T t);
}
