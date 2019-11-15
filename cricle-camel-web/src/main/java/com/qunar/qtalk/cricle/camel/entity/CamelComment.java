package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
public class CamelComment implements Serializable {
    /**
     * null
     */
    private Integer id;

    /**
     * null
     */
    @NotEmpty(message = "commentUUID不能为空")
    private String commentUUID;

    /**
     * null
     */
    @NotEmpty(message = "postUUID不能为空")
    private String postUUID;

    /**
     * null
     */
    private String parentCommentUUID;

    /**
     * null
     */
    @NotEmpty(message = "content不能为空")
    private String content;

    /**
     * null
     */
    @NotEmpty(message = "fromUser不能为空")
    private String fromUser;

    /**
     * null
     */
    private String toUser;

    /**
     * null
     */
    @NotEmpty(message = "fromHost不能为空")
    private String fromHost;

    /**
     * null
     */
    private String toHost;

    /**
     * null
     */
    private Timestamp createTime;

    /**
     * null
     */
    private Timestamp updateTime;

    /**
     * null
     */
    @NotEmpty(message = "isAnonymous不能为空")
    private short isAnonymous = 0;

    @NotEmpty(message = "postOwner不能为空")
    private String postOwner;

    @NotEmpty(message = "postOwnerHost不能为空")
    private String postOwnerHost;

    private String anonymousPhoto = "";

    private String anonymousName = "";

    /**
     * null
     */
    private Integer likeNum = 0;

    /**
     * null
     */
    private Integer isDelete;

    /**
     * null
     */
    private Short reviewStatus = 0;

    /**
     * null
     */
    private Short toisAnonymous = 0;
    private String toAnonymousName = "";
    private String toAnonymousPhoto = "";
    /**
     * 2.0新加字段，标识评论的超级父评论的uuid
     */
    private String superParentUUID;

    /**
     * 2.0 新加字段标识父评论的状态
     */
    private Integer commentStatus;

    /**
     * v2.0 新加字段热门评论的uuid
     */
    private List<String> hotCommentUUID;

    /**
     * v2.0 新加字段外带评论数
     */
    private Integer attachCommentCount = 5;

    /**
     * v3.0新加字段评论支持@功能
     */
    private String atList;
}