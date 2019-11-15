package com.qunar.qtalk.cricle.camel.common.event;

/**
 * Created by haoling.wang on 2019/1/15.
 */
public interface EventConsumer extends Event {

    void consumeEvent();

    void handleEvent(EventModel eventModel);
}
