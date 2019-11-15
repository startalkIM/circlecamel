package com.qunar.qtalk.cricle.camel.common.vo;

import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.common.dto.MessageContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/17.
 */
@Data
@AllArgsConstructor
@Builder
public class MessageRespVo implements Serializable {

    private List<MessageContent> msgList;

    private Integer total;

    public MessageRespVo() {
        this.msgList = Lists.newArrayList();
        this.total = 0;
    }
}
