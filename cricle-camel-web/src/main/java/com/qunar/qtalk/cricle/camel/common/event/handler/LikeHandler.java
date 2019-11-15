package com.qunar.qtalk.cricle.camel.common.event.handler;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.SendPush;
import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.dto.MessageContent;
import com.qunar.qtalk.cricle.camel.common.dto.UserModelDto;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.service.CamelMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/14.
 */
@Slf4j
@Component
public class LikeHandler extends BaseEventHandler {

    @Resource
    private DozerUtils dozerUtils;

    @Resource
    private CamelMessageService camelMessageService;

    @Resource
    private SendPush sendPush;

    @Override
    CamelMessage transEvent(EventModel eventModel) {
        Assert.assertArgNotNull(eventModel, "LikeHandler receive eventModel is null");

        CamelMessage camelMessage = dozerUtils.map(eventModel, CamelMessage.class);
        camelMessage.setUuid(IDUtils.getUUIDUpper());
        camelMessage.setFlag(MsgStatusEnum.UNREAD);

        MessageContent messageContent = new MessageContent();
        messageContent.setEventType(eventModel.getEventType().getType());
        messageContent.setPostUUID(eventModel.getPostUuid());
        messageContent.setUuid(camelMessage.getUuid());

        return camelMessage;
    }

    @Override
    CamelMessageService getCamelMessageService() {
        return camelMessageService;
    }

    @Override
    public boolean checkRepeatMsg(CamelMessage camelMessage) {
        return !camelMessageService.checkMessageIsRepeat(camelMessage);
    }

    @Override
    public boolean handleMessage(CamelMessage camelMessage) {
        // storage db
        JsonResult res = camelMessageService.saveCamelMessage(camelMessage);
        if (!res.isRet()) {
            log.error("message save to db fail,msg:{}", JSON.toJSONString(camelMessage));
        }
        // event notify
        UserModelDto userModelDto = UserModelDto.builder()
                .userName(camelMessage.getToUser()).userHost(camelMessage.getToHost()).build();
        log.info("JSON.toJSONString(camelMessage):{}",JSON.toJSONString(camelMessage));
        return sendPush.sendNotify(userModelDto, JSON.toJSONString(camelMessage));
    }


    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
