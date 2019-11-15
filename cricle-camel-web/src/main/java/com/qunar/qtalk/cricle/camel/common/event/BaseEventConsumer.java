package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.concurrent.CamelThreadFactory;
import com.qunar.qtalk.cricle.camel.common.exception.EventException;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/15.
 */
@Slf4j
@Component
public class BaseEventConsumer implements EventConsumer, InitializingBean, ApplicationContextAware {



    private ApplicationContext applicationContext;

    private Map<EventType, List<EventHandler>> handleMap = Maps.newHashMap();

    @Override
    public void consumeEvent() {
        return;
//        String fullKey = RedisAccessor.makeKey(KEY_PREFIX);
//        new CamelThreadFactory("event-consumer", true).newThread(() -> {
//            log.info("event-consumer 线程 start");
//            while (true) {
//                try {
//                    List<String> res = RedisAccessor.execute(key -> sedis.brpop(0, key), KEY_PREFIX);
//                    if (CollectionUtils.isNotEmpty(res)) {
//                        res.stream().filter(s -> !StringUtils.equals(fullKey, s))
//                                .forEach(msg -> {
//                                    try {
//                                        EventModel eventModel = JSONObject.parseObject(msg, EventModel.class);
//                                        handleEvent(eventModel);
//                                    } catch (Exception e) {
//                                        log.error("handle event msg fail,msg:{}", msg, e);
//                                    }
//                                });
//                    }
//                } catch (Exception e) {
//                    log.error("brpop event data occur exception", e);
//                }
//            }
//        }).start();
    }

    @Override
    public void handleEvent(EventModel eventModel) {
        EventType eventType = eventModel.getEventType();
        List<EventHandler> eventHandlers = handleMap.get(eventType);
        if (CollectionUtils.isEmpty(eventHandlers)) {
            throw new EventException(String.format("eventType:%s not have eventHandler", eventType.getName()));
        }
        eventHandlers.forEach(eventHandler -> eventHandler.doHandle(eventModel));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (MapUtils.isEmpty(beans)) {
            throw new IllegalStateException("current system don't have eventHandler");
        }
        beans.forEach((k, v) -> {
            List<EventType> supportEventTypes = v.getSupportEventTypes();
            supportEventTypes.stream().forEach(eventType -> {
                if (handleMap.containsKey(eventType)) {
                    handleMap.get(eventType).add(v);
                } else {
                    handleMap.put(eventType, Lists.newArrayList(v));
                }
            });
        });
        log.info("event handle map init finsh");
        consumeEvent();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
