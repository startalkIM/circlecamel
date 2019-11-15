package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/17.
 */
@Data
public class MessageReqVo implements Serializable {

    @NotNull(message = "请求消息人不能为空")
    private String user;

    @NotNull(message = "请求消息域不能为空")
    private String userHost;

    private String messageId;

    private Long messageTime;
}
