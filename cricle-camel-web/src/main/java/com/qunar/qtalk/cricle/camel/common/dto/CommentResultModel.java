package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import lombok.Data;

import java.util.List;

@Data
public class CommentResultModel {

    private List<CamelCommentV2Dto> newComment;
    private List<CamelDelete> deleteComments;
    private long postCommentNum;
    private long postLikeNum;
    private Integer isPostLike;
    private Integer returnType=0; //标识返回的是主评论还是子评论，0是主评论，1是子评论
    private List<CamelCommentDto> attachCommentList;

}
