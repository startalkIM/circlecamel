package com.qunar.qtalk.cricle.camel.common.event;

import com.qunar.qtalk.cricle.camel.common.exception.EventException;

/**
 * Created by haoling.wang on 2019/1/14.
 */
public interface EventProducer<T> extends Event {

    EventModel produceEvent(T t) throws EventException;

    boolean checkEventProduceNecessary(T t);

    void notifyEvent(T t);
}
