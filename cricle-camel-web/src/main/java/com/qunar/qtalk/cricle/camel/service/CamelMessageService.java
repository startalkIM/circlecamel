package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.dto.MessageContent;
import com.qunar.qtalk.cricle.camel.common.dto.MessageQueryDto;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.MessageReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.MessageRespVo;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.mapper.CamelMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haoling.wang on 2019/1/16.
 */
@Slf4j
@Service
public class CamelMessageService {

    @Resource
    private CamelMessageMapper camelMessageMapper;

    public JsonResult saveCamelMessage(CamelMessage camelMessage) {
        Assert.assertArgNotNull(camelMessage, "saveCamelMessage camelMessage is null");
        int i = camelMessageMapper.insertSelective(camelMessage);
        if (i == 1) {
            return JsonResultUtils.success(camelMessage);
        }
        return JsonResultUtils.fail(BaseCode.DB_ERROR.getCode(), BaseCode.DB_ERROR.getMsg());
    }

    /**
     * 校验消息是否已经存在
     *
     * @param camelMessage
     * @return true:存在
     */
    public Boolean checkMessageIsRepeat(CamelMessage camelMessage) {
        Assert.assertArgNotNull(camelMessage, "checkMessageIsRepeat camelMessage is null");
        Assert.assertArg(!Strings.isNullOrEmpty(camelMessage.getEntityId()), "checkMessageIsRepeat camelMessage entityId is null");
        Assert.assertArg(camelMessage.getEventType() != null, "checkMessageIsRepeat camelMessage eventType is null");
        CamelMessage camelMsg = camelMessageMapper.getByEventTypeAndEntityId(camelMessage);
        return camelMsg != null;
    }

    public JsonResult getMessageList(MessageReqVo messageReqVo) {
        MessageRespVo messageRespVo = new MessageRespVo();
        try {
            MessageQueryDto messageQueryDto = new MessageQueryDto();
            messageQueryDto.setUser(messageReqVo.getUser());
            messageQueryDto.setHost(messageReqVo.getUserHost());

            Long messageTime = messageReqVo.getMessageTime();

            if (messageTime != null && messageTime > 0L) {
                messageQueryDto.setTime(new Timestamp(messageTime));
            }

            List<CamelMessage> messageList = camelMessageMapper.getMessageList(messageQueryDto);

            if (CollectionUtils.isNotEmpty(messageList)) {
                List<MessageContent> msgList = messageList.stream()
                        .filter(camelMessage -> !StringUtils.equals(camelMessage.getUuid(), messageReqVo.getMessageId()))
                        .map(CamelMessage::getContent)
                        .map(content -> JSON.parseObject(content, MessageContent.class))
                        .collect(Collectors.toList());

                messageRespVo.setMsgList(msgList);
                messageRespVo.setTotal(msgList.size());
            }
        } catch (Exception e) {
            log.error("getMessageList occur exception,messageReqVo:{}", JSON.toJSONString(messageReqVo), e);
        }
        return JsonResultUtils.success(messageRespVo);
    }

    public Integer updateReadFlagByTime(Timestamp timestamp, String user, String host) {
        return camelMessageMapper.updateReadFlagByTime(timestamp, user, host);
    }

    public Integer updatePostAtMessage2DeleteFlag(List<EventType> eventTypes,String postUUID, List<String> userList) {
        int update = 0;
        try {
            if (CollectionUtils.isNotEmpty(eventTypes)) {
                for (EventType eventType : eventTypes) {
                    update += camelMessageMapper.updatePostAtMessage2DeleteFlag(eventType, postUUID, userList);
                }
            }
        } catch (Exception e) {
            log.error("updatePostAtMessage2DeleteFlag occur exception,eventType:{},postUUID:{},userList:{}", EventType.ATREMINDPOST, postUUID, Arrays.toString(userList.toArray()), e);
        }
        return update;
    }

    public Integer updateCommentAtMessage2DeleteFlag(String commentUUID, List<String> userList) {
        try {
            return camelMessageMapper.updateCommentAtMessage2DeleteFlag(EventType.ATREMINDCOMMENT, commentUUID);
        } catch (Exception e) {
            log.error("updateCommentAtMessage2DeleteFlag occur exception,eventType:{},postUUID:{},userList:{}", EventType.ATREMINDCOMMENT, commentUUID, Arrays.toString(userList.toArray()), e);
        }
        return 0;
    }
}
