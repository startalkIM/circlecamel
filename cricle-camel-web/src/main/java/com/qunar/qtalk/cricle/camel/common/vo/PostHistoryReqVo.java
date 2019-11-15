package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/3.
 */
@Data
public class PostHistoryReqVo implements Serializable {

    @NotNull(message = "当前请求的帖子id不能为空")
    private Integer curPostId;

    private Integer pageSize = 20;
}
