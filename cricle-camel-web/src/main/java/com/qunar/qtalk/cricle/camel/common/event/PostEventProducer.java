package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.exception.EventException;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.entity.CamelPostContent;
import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import com.qunar.qtalk.cricle.camel.mapper.CamelAuthMapper;
import com.qunar.qtalk.cricle.camel.service.CamelAtService;
import com.qunar.qtalk.cricle.camel.service.ConsumerEventService;
//import com.qunar.qtalk.cricle.camel.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.GET_USERS_FROM_REIDS;

/**
 * PostEventProducer
 *
 * @author binz.zhang
 * @date 2019/1/18
 */
@Slf4j
@Component
public class PostEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostEventProducer.class);

    @Value("${redis_user_list}")
    private String redisUserKey;

    @Resource
    private CamelAuthMapper camelAuthMapper;

    @Resource
    private ConsumerEventService consumerEventService;

    @Resource
    private RedisUtil redisUtil;

//    @Resource
//    private KafkaProducerService kafkaProducerService;

    @Autowired
    private CamelAtService camelAtService;

    protected RedisUtil getSedis() {
        return redisUtil;
    }

    public void produceEvent(CamelPost camelPostDto) throws EventException {
        camelAtService.actionAt(camelPostDto); //处理@相关的操作
        EventModel camelMessage = transvantMessage(camelPostDto);
        List<CamelUserModel> userList;
        Set<String> users = RedisAccessor.execute(key -> getSedis().smembers(key), redisUserKey);
        if (users != null && users.size() > 0) {
            camelMessage.setToUser(JacksonUtils.obj2String(users));
            consumerEventService.consumerNotify(camelMessage);
            //kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, JacksonUtils.obj2String(camelMessage));
            return;
        }
        userList = camelAuthMapper.selectLegalUser();
        if (userList != null && userList.size() > 0) {
            List<String> uu = userList.stream().map(x -> x.getUserName() + "@" + x.getUserHost()).collect(Collectors.toList());
            camelMessage.setToUser(JacksonUtils.obj2String(uu));
            consumerEventService.consumerNotify(camelMessage);
            //kafkaProducerService.synSendMessage(ContextConsts.KAFKA_SENDNOTIFY_KEY, JacksonUtils.obj2String(camelMessage));
            return;
        }
        log.error("new post send push fail,please check out the redis and the database");
    }

    public EventModel transvantMessage(CamelPost camelPostDto) {

        PostEventMsgContentModel msgContent = new PostEventMsgContentModel();
        EventModel camelMessage = new EventModel();
        camelMessage.setEventType(EventType.POST);
        camelMessage.setCreateTime(camelPostDto.getCreateTime());
        msgContent.setEventType(EventType.POST.getType());
        msgContent.setOwner(camelPostDto.getOwner());
        msgContent.setOwnerHost(camelPostDto.getOwnerHost());
        msgContent.setIsAnyonous(camelPostDto.getIsAnonymous());
        msgContent.setAnyonousName(camelPostDto.getAnonymousName());
        msgContent.setAnyonousPhoto(camelPostDto.getAnonymousPhoto());
        msgContent.setPostUUID(camelPostDto.getUuid());
        msgContent.setCreateTime(camelPostDto.getCreateTime());
        CamelPostContent content = JSON.parseObject(camelPostDto.getContent(), CamelPostContent.class);
        msgContent.setContent(getContent(content.getContent()));
        msgContent.setUuid(IDUtils.getUUIDUpper());
        camelMessage.setContent(JacksonUtils.obj2String(msgContent));
        return camelMessage;
    }

    private String getContent(String oriContent) {
        if (Strings.isNullOrEmpty(oriContent)) {
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
}