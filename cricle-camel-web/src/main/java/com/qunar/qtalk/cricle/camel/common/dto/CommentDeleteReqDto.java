package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentDeleteReqDto {

    private String commentUUID;
    private String postUUID;
    private String superParentUUID;
    private Integer attachCommentCount = 5;

}
