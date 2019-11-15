package com.qunar.qtalk.cricle.camel.common.event;

import lombok.Getter;
import lombok.Setter;

/**
 * CommentEventModel
 *
 * @author binz.zhang
 * @date 2019/1/16
 */
@Setter
@Getter
public class CommentEventModel extends EventModel {
    private String postOwner;
    private String ownerHost;
}
