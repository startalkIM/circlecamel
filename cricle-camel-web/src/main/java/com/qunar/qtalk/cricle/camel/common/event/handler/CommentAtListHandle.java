package com.qunar.qtalk.cricle.camel.common.event.handler;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.SendPush;
import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.dto.UserModelDto;
import com.qunar.qtalk.cricle.camel.common.event.CommentEventMsgContentModel;
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

@Slf4j
@Component
public class CommentAtListHandle extends BaseEventHandler {
    @Resource
    private SendPush sendPush;
    @Resource
    private DozerUtils dozerUtils;
    @Resource
    private CamelMessageService camelMessageService;

    @Override
    CamelMessage transEvent(EventModel eventModel) {
        Assert.assertArgNotNull(eventModel, "aTHandler receive eventModel is null");
        CommentEventMsgContentModel ps = JSON.parseObject(eventModel.getContent(), CommentEventMsgContentModel.class);
        CamelMessage camelMessage = dozerUtils.map(eventModel, CamelMessage.class);
        camelMessage.setFlag(MsgStatusEnum.UNREAD);
        camelMessage.setEventType(EventType.ATREMINDCOMMENT);
        camelMessage.setUuid(ps.getUuid());
        return camelMessage;
    }

    @Override
    CamelMessageService getCamelMessageService() {
        return null;
    }

    @Override
    public boolean handleMessage(CamelMessage camelMessage) {
        try {
            log.info("comment at message save into the db,camelMessage:{}",JSON.toJSONString(camelMessage));
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


    public boolean handleAtMessage(String msg) {
        CamelMessage camelMessage = JSON.parseObject(msg, CamelMessage.class); //@消息入库
        camelMessage.setUuid(IDUtils.getUUIDUpper());
        //TODO @消息落库
        return sendPush.sendMessage(msg);
    }

    @Override
    public boolean checkRepeatMsg(CamelMessage camelMessage) {
        return true;
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ATREMINDCOMMENT);
    }
}
