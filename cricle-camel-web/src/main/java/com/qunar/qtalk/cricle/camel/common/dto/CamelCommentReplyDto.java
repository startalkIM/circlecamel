package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import lombok.Data;

/**
 * '我的回复' 返回对象
 */
@Data
public class CamelCommentReplyDto extends CamelComment {

    private Integer replyType;

    private CamelPost camelPost;

//    private CamelComment camelComment;

    private Integer eventType; // 方便客户端兼容通知类的数据，固定值5

    private String uuid; //方便客户端兼容通知类型的数据，此uuid就是评论的uuid
}
