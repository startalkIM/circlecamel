package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 给后台管理系统，将帖子内容按照文本和图片分开
 */
@Data
public class CamelPostManageResultDto extends CamelPostDto {

    private PostContentDto postContent;

}
