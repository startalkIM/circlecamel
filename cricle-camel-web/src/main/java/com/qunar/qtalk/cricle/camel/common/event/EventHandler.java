package com.qunar.qtalk.cricle.camel.common.event;

import com.qunar.qtalk.cricle.camel.entity.CamelMessage;

import java.util.List;

/**
 * Created by haoling.wang on 2019/1/14.
 */
public interface EventHandler {

    void doHandle(EventModel eventModel);

    boolean checkRepeatMsg(CamelMessage camelMessage);

    List<EventType> getSupportEventTypes();
}
