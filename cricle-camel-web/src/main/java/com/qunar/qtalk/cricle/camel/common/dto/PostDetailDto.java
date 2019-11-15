package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 后台管理的帖子详情页
 */
@Data
public class PostDetailDto implements Serializable {

    private CamelPostManageResultDto post;

    private List<CommentManageDto> comments = Lists.newArrayList();
}
