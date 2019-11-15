package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/21.
 */
@Data
public class PostReqVo implements Serializable {

    private Long postCreateTime;

    private String owner;

    private String ownerHost;

    private Integer pageSize = 20;

    /**
     * 获取帖子的评论数
     */
    private Integer attachCommentCount = 5;
    /**
     * 是否拉取置顶贴子，为(1或者空):表示拉取置顶帖;0:表示不拉取置顶帖
     */
    private Integer getTop = 1;
    /**
     * 拉取的帖子状态，默认拉取正常、置顶、置热帖
     */
    private Integer postType = 7;

}

