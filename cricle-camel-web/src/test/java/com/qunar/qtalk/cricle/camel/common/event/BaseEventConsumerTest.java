package com.qunar.qtalk.cricle.camel.common.event;

import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Created by haoling.wang on 2019/1/16.
 */
public class BaseEventConsumerTest extends BaseTest {

    @Resource
    private BaseEventConsumer baseEventConsumer;

    @Resource
    private DozerUtils dozerUtils;

    @Test
    public void handleEvent(){

        EventModel eventModel = new EventModel();

        eventModel.setEventType(EventType.LIKE);
        eventModel.setToUser("***");
        eventModel.setToHost("ejarbhost");
        eventModel.setFromUser("admin");
        eventModel.setFromHost("ejarbhost");
        eventModel.setEntityId(IDUtils.getUUID());
        eventModel.setPostUuid(IDUtils.getUUID());
        eventModel.setCreateTime(DateUtils.getCurTimeStamp());
        HashMap map = Maps.newHashMap();
        map.put("likeNum", "1");
       // eventModel.setContent(map);
        baseEventConsumer.handleEvent(eventModel);
    }

    @Test
    public void testDozer(){
        EventModel eventModel = new EventModel();
        HashMap map = Maps.newHashMap();
        map.put("aaa", "fddd");

       // eventModel.setContent(map);

        CamelMessage message = dozerUtils.map(eventModel, CamelMessage.class);
        System.out.println(message.getContent());


    }
}

