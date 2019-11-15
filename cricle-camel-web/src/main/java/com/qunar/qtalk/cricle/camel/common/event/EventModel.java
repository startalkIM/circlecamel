package com.qunar.qtalk.cricle.camel.common.event;

import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by haoling.wang on 2019/1/14.
 * <p>
 * 事件模型
 */
@Data
@AllArgsConstructor
@Builder
public class EventModel implements Serializable {

    private EventType eventType; // 事件类型

    private String postUuid;

    private String entityId; // 实体id-> 点赞id或者评论id

    private String fromUser; // 触发者

    private String fromHost;

    private String toUser; // 接收者

    private String toHost;

    private String content;

    private HashMap ext;

    private Timestamp createTime; // 事件触发时间

    public EventModel() {
    }

    public EventModel(EventType eventType, String postId, String fromUser, String fromUserHost, Timestamp eventTime) {
        this.eventType = eventType;
        this.postUuid = postId;
        this.fromUser = fromUser;
        this.fromHost = fromUserHost;
        this.createTime = eventTime;
    }

    public EventModel(EventType eventType, String postId, String fromUser, String fromUserHost,
                      String toUser, String toUserHost, Timestamp eventTime) {
        this.eventType = eventType;
        this.postUuid = postId;
        this.fromUser = fromUser;
        this.fromHost = fromUserHost;
        this.toUser = toUser;
        this.toHost = toUserHost;
        this.createTime = eventTime;
    }
    public EventModel(EventType eventType, String postId, String fromUser, String fromUserHost,
                      String toUser, String toUserHost) {
        this.eventType = eventType;
        this.postUuid = postId;
        this.fromUser = fromUser;
        this.fromHost = fromUserHost;
        this.toUser = toUser;
        this.toHost = toUserHost;
    }

//    public void setContent(Object content) {
//        this.content = JacksonUtils.obj2String(content);
//    }
}
