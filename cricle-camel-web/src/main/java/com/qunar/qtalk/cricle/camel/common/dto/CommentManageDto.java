package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 驼圈后台展示
 * 帖子详情中的评论分级展示
 */
@Data
public class CommentManageDto extends CamelCommentDto {

    private List<CamelCommentDto> childComments;
}
