package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.event.handler.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author binz.zhang
 * @Date: 2019-11-14    17:42
 */
@Service
@Slf4j
public class ConsumerEventService {
    @Resource
    private PostHandler postHandler;

    @Resource
    private LikeHandler likeHandler;

    @Resource
    private CommentHandler commentHandler;
    @Resource
    private CommentAtListHandle commentAtListHandle;


    @Resource
    private PostAtListHandle postAtListHandle;


    public void consumerNotify(EventModel eventModel){
        switch (eventModel.getEventType()) {
            case POST:
                postHandler.doHandle(eventModel);
                break;
            case COMMENT:
                commentHandler.doHandle(eventModel);
                break;
            case LIKE:
                break;
            case ATREMINDPOST:
                postAtListHandle.doHandle(eventModel);
                break;
            case ATREMINDCOMMENT:
                commentAtListHandle.doHandle(eventModel);
                break;
        }
    }

    public void consumerMsg(String msg){
        commentAtListHandle.handleAtMessage(msg);
        return;
    }

}
