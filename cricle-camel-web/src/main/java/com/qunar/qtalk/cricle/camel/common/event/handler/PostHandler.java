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
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.service.CamelMessageService;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * PostHandler
 *
 * @author binz.zhang
 * @date 2019/1/18
 */
@Slf4j
@Component
public class PostHandler extends BaseEventHandler {
    @Resource
    private DozerUtils dozerUtils;

    @Resource
    private CamelMessageService camelMessageService;

    @Resource
    private SendPush sendPush;

    @Override
    CamelMessage transEvent(EventModel eventModel) {
        Assert.assertArgNotNull(eventModel, "LikeHandler receive eventModel is null");
        log.info("eventModel is:{}", JacksonUtils.obj2String(eventModel));
        log.info("post convert eventModel to camelMessage");
        PostEventMsgContentModel ps = JSON.parseObject(eventModel.getContent(),PostEventMsgContentModel.class);
        CamelMessage camelMessage = dozerUtils.map(eventModel, CamelMessage.class);
        camelMessage.setFlag(MsgStatusEnum.UNREAD);
        camelMessage.setEventType(EventType.POST);
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
        List<String> users = JSON.parseArray(camelMessage.getToUser(),String.class);
        return sendPush.bathSendNotify(users, camelMessage.getContent());
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.POST);
    }
}
