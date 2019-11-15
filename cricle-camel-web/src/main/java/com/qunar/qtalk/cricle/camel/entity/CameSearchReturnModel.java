package com.qunar.qtalk.cricle.camel.entity;


import lombok.Data;

import java.sql.Timestamp;

/**
 * 搜索返回发的数据模型
 */
@Data
public class CameSearchReturnModel {
   // private String ownerUser;
    private String userFrom;
    private String userFromHost;
    private String fromIsAnonymous;
    private String fromAnonymousName;
    private String fromAnonymousPhoto;
    private String userTo;
    private String userToHost;
    private String toIsAnonymous;
    private String toAnonymousName;
    private String toAnonymousPhoto;
    private Timestamp createTime;
    private String postUUID;
    private String commentUUID;
    private Integer eventType;  //3:发帖 4:评论
    private String content;
}
