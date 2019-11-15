package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * '我的回复'的返回对象
 */
@Data
public class ReplyResultDto implements Serializable {

    private List<CamelComment> newComment = Lists.newArrayList();

    private List<CamelDelete> deleteComments = Lists.newArrayList();
}
