package com.qunar.qtalk.cricle.camel.service.filter.post;

import com.qunar.qtalk.cricle.camel.service.filter.Filter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by haoling.wang on 2019/3/6.
 */
@Slf4j
public abstract class BasePostFilter<T, R> implements Filter<T, R> {

    protected abstract boolean filter(T t);

    protected abstract void getPostList(T t, R r);

    @Override
    public void doFilter(T t, R r) {
        try {
            if (filter(t)) {
                getPostList(t, r);
            }
        } catch (Exception e) {
            log.error("BasePostFilter doFilter occur exception", e);
        }
    }
}
