package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeResultDto implements Serializable {

    private String likeId;

    private String postId;

    private String commentId;

    private String userId;

    private String userHost;

    private Integer opType;

    private Integer isLike; // 1:点赞 0:未点赞

    private Long likeNum;

    /**
     * v2.0 新加的字段
     */
    private List<CamelCommentDto> attachCommentList;

}
