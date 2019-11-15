package com.qunar.qtalk.cricle.camel.service.filter;

/**
 * Created by haoling.wang on 2019/3/7.
 */
public interface ServiceFilter<T,P,R> {

    void addServiceFilter(T t);

    R doFilter();

    void setParameter(P p);
}
