package com.qunar.qtalk.cricle.camel.common.event.handler;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.SendPush;
import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.dto.UserModelDto;
import com.qunar.qtalk.cricle.camel.common.event.CommentEventMsgContentModel;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.event.PostEventMsgContentModel;
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
 * Created by binz.zhang on 2019/1/14.
 */
@Slf4j
@Component
public class CommentHandler extends BaseEventHandler {
    @Resource
    private DozerUtils dozerUtils;

    @Resource
    private CamelMessageService camelMessageService;

    @Resource
    private SendPush sendPush;

    @Override
    CamelMessage transEvent(EventModel eventModel) {
        Assert.assertArgNotNull(eventModel, "LikeHandler receive eventModel is null");
        log.info("psot eventModel is :{}",eventModel);
       log.info("comment trans event to CamelMessge mode ");
       CommentEventMsgContentModel ps = JSON.parseObject(eventModel.getContent(),CommentEventMsgContentModel.class);
        CamelMessage camelMessage = dozerUtils.map(eventModel, CamelMessage.class);
        camelMessage.setFlag(MsgStatusEnum.UNREAD);
        camelMessage.setEventType(EventType.COMMENT);
        camelMessage.setUuid(ps.getUuid());
        return camelMessage;
    }

    @Override
    CamelMessageService getCamelMessageService() {
        return camelMessageService;
    }


    @Override
    public boolean checkRepeatMsg(CamelMessage camelMessage) {
        return true;
    }

    @Override
    public boolean handleMessage(CamelMessage camelMessage) {
        // storage db
        try {
            JsonResult res = camelMessageService.saveCamelMessage(camelMessage);
            if (!res.isRet()) {
                log.error("message save to db fail,msg:{},{}", JSON.toJSONString(camelMessage), res.getErrmsg());
            }
        } catch (Exception e) {
            log.error("message save to db fail,msg:{}", JSON.toJSONString(camelMessage), e);
        }
        UserModelDto userModelDto = UserModelDto.builder().userName(camelMessage.getToUser()).userHost(camelMessage.getToHost()).build();
        sendPush.sendNotify(userModelDto, camelMessage.getContent());
        return true;
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
