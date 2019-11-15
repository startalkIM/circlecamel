package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.dto.SendMessageParam;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.service.CamelAtService;
import com.qunar.qtalk.cricle.camel.service.ConsumerEventService;
//import com.qunar.qtalk.cricle.camel.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * CommentEventProducer
 *
 * @author binz.zhang
 * @date 2019/1/16
 */
@Slf4j
@Component
public class CommentEventProducer {

    public static final Integer limitLenthComment = 30;
//
//    @Resource
//    private KafkaProducerService kafkaProducerService;

    @Resource
    private CamelCommentMapper camelCommentMapper;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CamelAtService camelAtService;
    @Resource
    private ConsumerEventService consumerEventService;

    protected RedisUtil getSedis() {
        return redisUtil;
    }


    public void produceEvent(CamelComment camelCommentDto) {
        //处理@功能
        List<SendMessageParam.ToEntity> atUsers = camelAtService.actionAt(camelCommentDto);
        EventModel eventModelForCommt;
        String userTo = camelCommentDto.getToUser();
        String owner = camelCommentDto.getPostOwner();
        log.info("produce comment event->start");
        // @用户之前呢已经下发了通知了，这儿就不在下发了
        List<String> alreadySent = new ArrayList<>();
        List<String> atSent = new ArrayList<>();
        if(atUsers!=null && atUsers.size()>0){
            atUsers.stream().forEach(x->{
                alreadySent.add(x.getUser());
                atSent.add(x.getUser());
            });
        }
         if (!Strings.isNullOrEmpty(userTo) && !userTo.equals(camelCommentDto.getFromUser()) && !atSent.contains(userTo)) {
            log.info("produce comment event->userTo");
            alreadySent.add(userTo);
            eventModelForCommt = new EventModel(EventType.COMMENT, camelCommentDto.getPostUUID(),
                    camelCommentDto.getFromUser(), camelCommentDto.getFromHost(),
                    userTo, camelCommentDto.getToHost(), camelCommentDto.getCreateTime());
            generateMsg(camelCommentDto, eventModelForCommt);
            //String s = JSON.toJSONString(eventModelForCommt);
             consumerEventService.consumerNotify(eventModelForCommt);
            //kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, s);
        }
        if (!Strings.isNullOrEmpty(owner) && !owner.equals(userTo)
                && !owner.equals(camelCommentDto.getFromUser()) && !atSent.contains(owner)) {
            log.info("produce comment event->owner");
            alreadySent.add(owner);
            eventModelForCommt = new EventModel(EventType.COMMENT, camelCommentDto.getPostUUID(),
                    camelCommentDto.getFromUser(), camelCommentDto.getFromHost(),
                    owner, camelCommentDto.getPostOwnerHost(), camelCommentDto.getCreateTime());
            generateMsg(camelCommentDto, eventModelForCommt);
            consumerEventService.consumerNotify(eventModelForCommt);

            // String s = JSON.toJSONString(eventModelForCommt);
           // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, s);

        }
        //增加mark功能，只要帖子下的一级评论者都会收到其他一级评论的消息通知
        List<String> user;
        if (Strings.isNullOrEmpty(camelCommentDto.getToUser())) {
            user = camelCommentMapper.selectSuperParentCommentUser(camelCommentDto.getPostUUID());
        } else {
            user = camelCommentMapper.selectUserBySuperParentUUID(camelCommentDto.getPostUUID(), camelCommentDto.getSuperParentUUID());
        }
        if (CollectionUtils.isEmpty(user)) {
            return;
        }
        log.warn("already send {}", JSONUtils.toJSONString(alreadySent));
        user.removeAll(alreadySent);
        user.remove(camelCommentDto.getFromUser()); //去除本人的通知
        log.warn("mark to user {}", JSONUtils.toJSONString(user));

        EventModel eventModelForCommt1 = new EventModel(EventType.COMMENT, camelCommentDto.getPostUUID(),
                camelCommentDto.getFromUser(), camelCommentDto.getFromHost(),
                owner, camelCommentDto.getPostOwnerHost(), camelCommentDto.getCreateTime());
        user.stream().forEach(x -> {
            generateMsg(camelCommentDto, eventModelForCommt1); //确保对每个用户生成的消息UUID是不同的，所以要放在for循环中
            EventModel finalEventModelForCommt = eventModelForCommt1;
            finalEventModelForCommt.setToUser(x);
            consumerEventService.consumerNotify(finalEventModelForCommt);

           // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, JSON.toJSONString(finalEventModelForCommt));
        });

    }

    public void generateMsg(CamelComment camelCommentDto, EventModel eventModel) {
        CommentEventMsgContentModel msg  = new CommentEventMsgContentModel();
        msg.setEventType(EventType.COMMENT.getType());
        msg.setPostUUID(camelCommentDto.getPostUUID());
        msg.setUserFrom(camelCommentDto.getFromUser());
        msg.setUserFromHost(camelCommentDto.getFromHost());
        msg.setFromIsAnonymous(camelCommentDto.getIsAnonymous());
        msg.setFromAnonymousName(camelCommentDto.getAnonymousName());
        msg.setFromAnonymousPhoto(camelCommentDto.getAnonymousPhoto());
        msg.setToIsAnonymous(camelCommentDto.getToisAnonymous());
        msg.setToAnonymousName(camelCommentDto.getToAnonymousName());
        msg.setToAnonymousPhoto(camelCommentDto.getToAnonymousPhoto());
        if(camelCommentDto.getToUser()!=null){
            msg.setUserTo(camelCommentDto.getToUser());
        }if(camelCommentDto.getToHost()!=null){
            msg.setUserToHost(camelCommentDto.getToHost());
        }

        msg.setCreateTime(camelCommentDto.getCreateTime());
        msg.setReadState(0);
        msg.setUuid(IDUtils.getUUIDUpper()); //该方法不支持幂等性
        if (camelCommentDto.getContent().length() < CommentEventProducer.limitLenthComment) {
            msg.setContent(camelCommentDto.getContent());
        } else {
            msg.setContent(camelCommentDto.getContent().substring(0, CommentEventProducer.limitLenthComment));
        }
        eventModel.setEntityId(camelCommentDto.getCommentUUID());
        eventModel.setPostUuid(camelCommentDto.getPostUUID());
        eventModel.setContent(JacksonUtils.obj2String(msg));
    }

}
