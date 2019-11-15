package com.qunar.qtalk.cricle.camel.common.event;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.consts.LikeTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import com.qunar.qtalk.cricle.camel.common.dto.LikeDto;
import com.qunar.qtalk.cricle.camel.common.exception.EventException;
import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelCommentService;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by haoling.wang on 2019/1/14.
 */
@Slf4j
@Component
public class LikeEventProducer extends BaseEventProducer<LikeDto> {

    @Resource
    private CamelLikeService camelLikeService;

    @Resource
    private CamelPostService camelPostService;

    @Resource
    private CamelCommentService camelCommentService;


    @Resource
    private RedisUtil redisUtil;

    @Override
    public EventModel produceEvent(LikeDto likeDto) throws EventException {
//        try {
//            EventModel eventModel = new EventModel(EventType.LIKE, likeDto.getPostId(), likeDto.getUserId(), likeDto.getUserHost(), DateUtils.getCurTimeStamp());
//
//            OpTypeEnum opTypeEnum = likeDto.getOpTypeEnum();
//            Long likeNum = 0L;
//            // 点赞数
//            if (opTypeEnum == OpTypeEnum.OP_POST) {
//                CamelPost camelPost = camelPostService.getCamelPost(likeDto.getPostId());
//                String owner = camelPost.getOwner();
//                String host  = camelPost.getOwnerHost();
//                eventModel.setToUser(owner);
//                eventModel.setToHost(host);
//                likeNum = camelLikeService.getPostLikeCount(likeDto.getPostId());
//            } else if (opTypeEnum == OpTypeEnum.OP_COMMENT) {
//                CamelComment comment = camelCommentService.getCamelCommentByUUID(likeDto.getCommentId());
//                String fromUser = comment.getFromUser();
//                String fromHost = comment.getFromHost();
//                eventModel.setToUser(fromUser);
//                eventModel.setToHost(fromHost);
//                likeNum = camelLikeService.getCommentLikeCount(likeDto.getCommentId());
//            }
//            eventModel.setEntityId(likeDto.getLikeId());
//            eventModel.getContent().put("likeNum", String.valueOf(likeNum));
//            return eventModel;
//        } catch (Exception e) {
//            log.error("likeDto trans to EventModel fail,likeDto:{}", JSON.toJSONString(likeDto), e);
//            throw new EventException(String.format("likeDto trans to EventModel fail,likeId:%s", likeDto.getLikeId()));
//        }
        return null;
    }

    @Override
    protected RedisUtil getSedis() {
        return redisUtil;
    }

    @Override
    public boolean checkEventProduceNecessary(LikeDto likeDto) {
        if (likeDto.getLikeTypeEnum() == LikeTypeEnum.LIKE) {
            return true;
        }
        return false;
    }
}
