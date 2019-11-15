package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by haoling.wang on 2019/3/4.
 */
@Data
public class CamelPostAttachCommentDto extends CamelPostDto {

    private List<CamelCommentDto> attachCommentList;
}
