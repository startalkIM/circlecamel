package com.qunar.qtalk.cricle.camel.common.event;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 发帖时 push给客户端的消息内容
 */

@Data
public class PostEventMsgContentModel {
    private String postUUID;
    private String owner="";
    private String ownerHost="";
    private Integer isAnyonous;
    private String anyonousName="";
    private String anyonousPhoto="";
    private Timestamp createTime;
    private Integer eventType;
    private String  content="";
    private String uuid; //本条消息的UUID
}
