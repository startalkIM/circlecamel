package com.qunar.qtalk.cricle.camel.common.event;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AtPostEventMsgContentModel extends PostEventMsgContentModel {
    private Integer readState;
    private String postUUID;
    private String owner;
    private String ownerHost;
    private Integer isAnyonous;
    private String anyonousName;
    private String anyonousPhoto;
    private Timestamp createTime;
    private Integer eventType;
    private String  content;
    private String uuid; //本条消息的UUID
}
