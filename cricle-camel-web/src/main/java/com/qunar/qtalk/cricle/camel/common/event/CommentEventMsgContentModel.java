package com.qunar.qtalk.cricle.camel.common.event;

import lombok.Data;

import java.sql.Timestamp;


/**
 * 评论 push给客户端的内容
 */
@Data
public class CommentEventMsgContentModel {

    private String userFrom;
    private String userFromHost;
    private short fromIsAnonymous;
    private String fromAnonymousName="";
    private String fromAnonymousPhoto = "";
    private String userTo="";
    private String userToHost="";
    private short toIsAnonymous;
    private String toAnonymousName="";
    private String toAnonymousPhoto="";
    private String postUUID;
    private Timestamp createTime;
    private Integer readState;
    private Integer eventType;
    private String  content;
    private String uuid; //本条消息的UUID

}
