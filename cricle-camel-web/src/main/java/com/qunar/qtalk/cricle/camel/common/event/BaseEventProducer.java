package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;


/**
 * Created by haoling.wang on 2019/1/15.
 */
public abstract class BaseEventProducer<T> implements EventProducer<T> {

    protected abstract RedisUtil getSedis();

    @Override
    public void notifyEvent(T t) {
        if (checkEventProduceNecessary(t)) {
            EventModel eventModel = produceEvent(t);
            String s = JSON.toJSONString(eventModel);
            RedisAccessor.execute(key -> getSedis().lpush(key, s), KEY_PREFIX);
        }
    }
}
