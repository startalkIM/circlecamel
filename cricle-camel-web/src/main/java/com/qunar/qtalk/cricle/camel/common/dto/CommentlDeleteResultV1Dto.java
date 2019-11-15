package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

/**
 * V1.0版本的删除返回model
 */

@Data
public class CommentlDeleteResultV1Dto {
    private long postCommentNum;
    private long postLikeNum;
    private String commentUUID;
    private Integer isDelete;
}
