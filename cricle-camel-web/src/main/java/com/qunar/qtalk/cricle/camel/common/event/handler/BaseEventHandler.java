package com.qunar.qtalk.cricle.camel.common.event.handler;

import com.qunar.qtalk.cricle.camel.common.event.EventHandler;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.service.CamelMessageService;

/**
 * Created by haoling.wang on 2019/1/17.
 */
public abstract class BaseEventHandler implements EventHandler {

    abstract CamelMessage transEvent(EventModel eventModel);

    abstract CamelMessageService getCamelMessageService();

    @Override
    public void doHandle(EventModel eventModel) {
        CamelMessage camelMessage = transEvent(eventModel);
        if (checkRepeatMsg(camelMessage)) {
            handleMessage(camelMessage);
        }
    }

    /**
     * 如不需要默认实现，子类可重写此方法
     *
     * @param camelMessage
     * @return true:处理成功
     */
    public boolean handleMessage(CamelMessage camelMessage) {
        return true;
    }

}