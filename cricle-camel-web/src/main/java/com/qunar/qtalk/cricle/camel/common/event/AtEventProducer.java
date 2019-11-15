package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.dto.SendMessageParam;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.entity.CamelPostContent;
import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import com.qunar.qtalk.cricle.camel.mapper.CamelAuthMapper;
import com.qunar.qtalk.cricle.camel.service.ConsumerEventService;
//import com.qunar.qtalk.cricle.camel.service.KafkaProducerService;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.qunar.qtalk.cricle.camel.common.event.CommentEventProducer.limitLenthComment;

@Component
@Slf4j
public class AtEventProducer {
    private static final String CIRCLE_CAMEL_ASSISTANT = "circle_camel_root";


//    @Autowired
//    private KafkaProducerService kafkaProducerService;
    @Autowired
    private ConsumerEventService consumerEventService;

    @Autowired
    private PostEventProducer postEventProducer;
    @Autowired
    private CommentEventProducer commentEventProducer;

    @Resource
    private CamelAuthMapper camelAuthMapper;

    public void produceEvent(CamelPost camelPost, List<SendMessageParam.ToEntity> users) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        log.info("sent at notify");
        users.stream().forEach(x -> {
            //send push to at users
            EventModel camelMessage = transvantMessage(camelPost);
            camelMessage.setToUser(x.getUser());
            camelMessage.setToHost(x.getHost());
            consumerEventService.consumerNotify(camelMessage);
           // String s = JSON.toJSONString(camelMessage);
           // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, s);
        });
        // send message to at users
        sendAtPostMsg(camelPost,users);
    }


    public void produceEvent(CamelComment camelComment,List<SendMessageParam.ToEntity> users){
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        users.stream().forEach(x -> {
            EventModel camelMessage = transvantMessage(camelComment);
            camelMessage.setToUser(x.getUser());
            camelMessage.setToHost(x.getHost());
            consumerEventService.consumerNotify(camelMessage);

            // String s = JSON.toJSONString(camelMessage);
            //kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, s);
        });
        sendAtCommentMsg(camelComment,users);
    }



    private void sendAtPostMsg(CamelPost camelPost,List<SendMessageParam.ToEntity> users){
        SendMessageParam sendMessageParam = new SendMessageParam();
        sendMessageParam.setFrom(CIRCLE_CAMEL_ASSISTANT);
        sendMessageParam.setFromhost("ejabhost1");
        sendMessageParam.setTo(users);
        sendMessageParam.setType("chat");
        sendMessageParam.setMsgtype("258"); //驼圈的@x消息提醒 消息类型258
        sendMessageParam.setSystem("vs_circle_camel");
        sendMessageParam.setContent("您收到一条新的驼圈@消息提醒,请升级客户端查看!");
        /**
         * extendInfo内容
         */
        JSONObject extInfo = new JSONObject();
        List<JSONObject> keyValues = new ArrayList<>();
        JSONObject postOwner = new JSONObject(1);
        JSONObject postContent = new JSONObject(1);
        JSONObject postTime = new JSONObject(1);
        JSONObject param = new JSONObject(2);



        if(camelPost.getIsAnonymous().equals(0)){
            CamelUserModel ow = camelAuthMapper.selectUserModel(camelPost.getOwner(),1);
             postOwner.put("发帖人",ow.getUserCName());
            keyValues.add(postOwner);
        }else {
            postOwner.put("发帖人",camelPost.getAnonymousName());
            keyValues.add(postOwner);
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        postTime.put("发帖时间",sdf.format(camelPost.getCreateTime()));
        keyValues.add(postTime);

        CamelPostContent content = JSON.parseObject(camelPost.getContent(), CamelPostContent.class);
        if(!Strings.isNullOrEmpty(content.getContent())){
            postContent.put("帖子内容",getContent(content.getExContent()));
            keyValues.add(postContent);
        }else {
            postContent.put("帖子内容",getContent(content.getContent()));
            keyValues.add(postContent);
        }
        extInfo.put("keyValues",keyValues);
        param.put("postUUID",camelPost.getUuid());
        param.put("eventType",EventType.ATREMINDPOST.getType());
        extInfo.put("params",param);
        extInfo.put("title","您收到一条驼圈@提醒");
        sendMessageParam.setExtendinfo(extInfo.toJSONString());
        consumerEventService.consumerMsg(JacksonUtils.obj2String(sendMessageParam));

        // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDMESSAGE_KEY, JacksonUtils.obj2String(sendMessageParam));

    }

    private void sendAtCommentMsg(CamelComment camelComment,List<SendMessageParam.ToEntity> users){
        SendMessageParam sendMessageParam = new SendMessageParam();
        sendMessageParam.setFrom(CIRCLE_CAMEL_ASSISTANT);
        sendMessageParam.setFromhost("ejabhost1");
        sendMessageParam.setTo(users);
        sendMessageParam.setType("chat");
        sendMessageParam.setMsgtype("258"); //驼圈的@x消息提醒 消息类型258
        sendMessageParam.setSystem("vs_circle_camel");
        sendMessageParam.setContent("您收到一条新的驼圈@消息提醒,请升级客户端查看!");
        /**
         * extendInfo内容
         */
        JSONObject extInfo = new JSONObject();
        List<JSONObject> keyValues = new ArrayList<>();
        JSONObject postOwner = new JSONObject(1);
        JSONObject postContent = new JSONObject(1);
        JSONObject postTime = new JSONObject(1);
        JSONObject param = new JSONObject(2);

        if(camelComment.getIsAnonymous()==0){
            CamelUserModel ow = camelAuthMapper.selectUserModel(camelComment.getFromUser(),1);
            postOwner.put("@发起人",ow.getUserCName());
            keyValues.add(postOwner);
        }else {
            postOwner.put("@发起人",camelComment.getAnonymousName());
            keyValues.add(postOwner);
        }

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        postTime.put("发帖时间",sdf.format(camelComment.getCreateTime()));
        keyValues.add(postTime);

        postContent.put("评论内容",getContent(camelComment.getContent()));
        keyValues.add(postContent);

        // CamelPostContent content = JSON.parseObject(camelComment.getContent(), CamelPostContent.class);
//        if(!Strings.isNullOrEmpty(camelComment.getContent())){
//            postContent.put("评论内容",getContent(content.getExContent()));
//            keyValues.add(postContent);
//        }else {
//            postContent.put("评论内容",getContent(content.getContent()));
//            keyValues.add(postContent);
//        }
        extInfo.put("keyValues",keyValues);
        param.put("postUUID",camelComment.getPostUUID());
        param.put("eventType",EventType.ATREMINDCOMMENT.getType());
        extInfo.put("params",param);
        extInfo.put("title","您收到一条驼圈@提醒");
        sendMessageParam.setExtendinfo(extInfo.toJSONString());
        consumerEventService.consumerMsg(JacksonUtils.obj2String(sendMessageParam));
        // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDMESSAGE_KEY, JacksonUtils.obj2String(sendMessageParam));

    }



    private void sendMessage(String content,List<SendMessageParam.ToEntity> users){
        SendMessageParam sendMessageParam = new SendMessageParam();
        sendMessageParam.setFrom(CIRCLE_CAMEL_ASSISTANT);
        sendMessageParam.setFromhost("ejabhost1");
        sendMessageParam.setTo(users);
        sendMessageParam.setType("chat");
        sendMessageParam.setMsgtype("258"); //驼圈的@x消息提醒 消息类型258
        sendMessageParam.setSystem("vs_circle_camel");
        sendMessageParam.setContent(content);
        consumerEventService.consumerMsg(JacksonUtils.obj2String(sendMessageParam));
       // kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDMESSAGE_KEY, JacksonUtils.obj2String(sendMessageParam));
    }

    public EventModel transvantMessage(CamelPost camelPostDto) {

        CommentEventMsgContentModel msgContent = new CommentEventMsgContentModel();
        EventModel  camelMessage = new EventModel();
        camelMessage.setFromUser(camelPostDto.getOwner());
        camelMessage.setFromHost(camelPostDto.getOwnerHost());
        camelMessage.setPostUuid(camelPostDto.getUuid());
        camelMessage.setEventType(EventType.ATREMINDPOST);
        camelMessage.setCreateTime(camelPostDto.getCreateTime());
        msgContent.setEventType(EventType.ATREMINDPOST.getType());
        msgContent.setUserFrom(camelPostDto.getOwner());
        msgContent.setUserFromHost(camelPostDto.getOwnerHost());
        msgContent.setFromIsAnonymous(camelPostDto.getIsAnonymous().shortValue());
        msgContent.setFromAnonymousName(camelPostDto.getAnonymousName());
        msgContent.setFromAnonymousPhoto(camelPostDto.getAnonymousPhoto());
        msgContent.setPostUUID(camelPostDto.getUuid());
        msgContent.setReadState(0);
        msgContent.setCreateTime(camelPostDto.getCreateTime());
        CamelPostContent content = JSON.parseObject(camelPostDto.getContent(), CamelPostContent.class);
        msgContent.setContent(getContent(content.getContent()));
        msgContent.setUuid(IDUtils.getUUIDUpper());
        camelMessage.setContent(JacksonUtils.obj2String(msgContent));
        return camelMessage;
    }
    private String getContent(String oriContent) {
        if (com.google.common.base.Strings.isNullOrEmpty(oriContent)) {
            return "图片分享";
        }
        if (oriContent.length() < ContextConsts.POST_NOTIFY_LIMIT) {
            return oriContent;
        }
        if (oriContent.length() >= ContextConsts.POST_NOTIFY_LIMIT) {
            return oriContent.substring(0, ContextConsts.POST_NOTIFY_LIMIT - 1);
        }
        return oriContent;
    }

    public  EventModel transvantMessage(CamelComment camelCommentDto) {
        EventModel eventModel = new EventModel();
        eventModel.setFromUser(camelCommentDto.getFromUser());
        eventModel.setFromHost(camelCommentDto.getFromHost());
        eventModel.setPostUuid(camelCommentDto.getPostUUID());
        eventModel.setCreateTime(camelCommentDto.getCreateTime()); //创建时间与@时间保持一致
        eventModel.setEventType(EventType.ATREMINDCOMMENT);
        CommentEventMsgContentModel msg  = new CommentEventMsgContentModel();
        msg.setEventType(EventType.ATREMINDCOMMENT.getType());
        msg.setPostUUID(camelCommentDto.getPostUUID());
        msg.setUserFrom(camelCommentDto.getFromUser());
        msg.setUserFromHost(camelCommentDto.getFromHost());
        msg.setFromIsAnonymous(camelCommentDto.getIsAnonymous());
        msg.setFromAnonymousName(camelCommentDto.getAnonymousName());
        msg.setUserFromHost(camelCommentDto.getFromHost());
        msg.setFromAnonymousPhoto(camelCommentDto.getAnonymousPhoto());
        msg.setToIsAnonymous(camelCommentDto.getToisAnonymous());
        msg.setToAnonymousName(camelCommentDto.getToAnonymousName());
        if(camelCommentDto.getToUser()!=null) {
            msg.setUserTo(camelCommentDto.getToUser());
        }
        if(camelCommentDto.getToHost()!=null){
            msg.setUserToHost(camelCommentDto.getToHost());
        }
        msg.setToAnonymousPhoto(camelCommentDto.getToAnonymousPhoto());
        msg.setCreateTime(camelCommentDto.getCreateTime());
        msg.setReadState(0);
        msg.setUuid(IDUtils.getUUIDUpper());
        if (camelCommentDto.getContent().length() < limitLenthComment) {
            msg.setContent(camelCommentDto.getContent());
        } else {
            msg.setContent(camelCommentDto.getContent().substring(0, limitLenthComment));
        }
        eventModel.setEventType(EventType.ATREMINDCOMMENT);
        eventModel.setEntityId(camelCommentDto.getCommentUUID());
        eventModel.setPostUuid(camelCommentDto.getPostUUID());
        eventModel.setContent(JacksonUtils.obj2String(msg));
        return eventModel;
    }
}
