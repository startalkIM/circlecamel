package com.qunar.qtalk.cricle.camel.service.filter;

/**
 * Created by haoling.wang on 2019/3/6.
 */
public interface Filter<T, R> {

    void doFilter(T t, R r);
}
