package com.qunar.qtalk.cricle.camel.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CamelLike {
    /**
     * null
     */
    private Integer id;

    /**
     * null
     */
    private String likeUuid;

    /**
     * null
     */
    private String likeOwner;

    /**
     * null
     */
    private String ownerHost;

    /**
     * null
     */
    private String postUuid;

    /**
     * null
     */
    private String commentUuid;

    /**
     * null
     */
    private Boolean deleteFlag;

    /**
     * null
     */
    private Timestamp updateTime;

    /**
     * null
     */
    private Timestamp creatTime;

    /**
     * null
     */
    private Boolean anonymousFlag;

    /**
     * null
     */
    private String anonymous;

}