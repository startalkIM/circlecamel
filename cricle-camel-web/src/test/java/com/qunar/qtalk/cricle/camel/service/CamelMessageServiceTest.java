package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.mapper.CamelMessageMapper;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by haoling.wang on 2019/1/16.
 */
public class CamelMessageServiceTest extends BaseTest {

    @Resource
    private CamelMessageService camelMessageService;

    @Resource
    private CamelMessageMapper camelMessageMapper;

    @Test
    public void saveCamelMessage() {
        CamelMessage camelMessage = new CamelMessage();
        camelMessage.setUuid(IDUtils.getUUID().toUpperCase());
        camelMessage.setContent("test");
        camelMessage.setFlag(MsgStatusEnum.UNREAD);
        camelMessage.setCreateTime(DateUtils.getCurTimeStamp());


        camelMessage.setEventType(EventType.LIKE);
        camelMessage.setEntityId(IDUtils.getUUID().toUpperCase());
        camelMessage.setPostUuid(IDUtils.getUUID().toUpperCase());
        camelMessage.setFromHost("djfsl");
        camelMessage.setFromUser("fdjsljfa");
        camelMessage.setToUser("fjdslfj");
        camelMessage.setToHost("fdjslfjlasjdflkjsdlkfj");
        camelMessageService.saveCamelMessage(camelMessage);
    }

    @Test
    public void get(){
        CamelMessage camelMessage = new CamelMessage();
        camelMessage.setEventType(EventType.COMMENT);
        camelMessage.setEntityId("123");
        CamelMessage camelMsg = camelMessageMapper.getByEventTypeAndEntityId(camelMessage);

        System.out.println(camelMsg);

        System.out.println(camelMsg.getContent());

        JSONObject jsonObject = JSON.parseObject(camelMsg.getContent());
        System.out.println(jsonObject.toString());
    }
}