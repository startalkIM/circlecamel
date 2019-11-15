package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.common.consts.LikeTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Data
public class LikeDto implements Serializable {

    private String userId;

    private String userHost;

    private OpTypeEnum opTypeEnum;

    private LikeTypeEnum likeTypeEnum;

    private String postId;

    private String commentId;

    private String likeId;

    /**
     * 2.0新加字段，标识评论的超级父评论的uuid
     */
    private String superParentUUID;

    private Integer attachCommentCount = 5;


}
