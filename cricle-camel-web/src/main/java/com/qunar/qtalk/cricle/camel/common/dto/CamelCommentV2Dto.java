package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 2.0接口 返回评论时带上其二级评论
 */

@Data
public class CamelCommentV2Dto extends CamelCommentDto {
    List<CamelCommentDto> newChild;
    List<CamelDelete> deleteChild;
    Integer postCommentNum;
    Integer postLikeNum;

    public CamelCommentV2Dto() {
        List<CamelCommentDto> newChild = new LinkedList<>();
        this.newChild = newChild;
    }
}
