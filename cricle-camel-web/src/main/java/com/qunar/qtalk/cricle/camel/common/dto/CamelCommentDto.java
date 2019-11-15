package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import lombok.Data;

/**
 * CamelCommentDto
 *
 * @author binz.zhang
 * @date 2019/1/10
 */
@Data
public class CamelCommentDto extends CamelComment {
    private Integer isLike; // 1:点赞 0:未点赞

    private String ownerName;

}
