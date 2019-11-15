package com.qunar.qtalk.cricle.camel.entity;

import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
public class CamelMessage implements Serializable {
    /**
     * null
     */
    private Integer id;

    /**
     * msg序号
     */
    private String uuid;

    /**
     * 事件类型,0:帖子，1：评论，2：点赞
     */
    private EventType eventType;

    /**
     * from user
     */
    private String fromUser;

    /**
     * null
     */
    private String fromHost;

    /**
     * null
     */
    private String toUser;

    /**
     * null
     */
    private String toHost;

    /**
     * null
     */
    private Timestamp createTime;

    /**
     * 事件内容
     */
    private String content;

    /**
     * 已读标记，0未读 1已读
     *  新增@功能之后，加入2表示此消息处理删除状态（@功能复用消息表）
     */
    private MsgStatusEnum flag;

    /**
     * 帖子uuid
     */
    private String postUuid;

    /**
     * 实体id,点赞id或者评论id
     */
    private String entityId;

}