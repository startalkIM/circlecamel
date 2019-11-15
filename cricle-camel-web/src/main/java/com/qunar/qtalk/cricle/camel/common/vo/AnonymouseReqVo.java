package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/2.
 */
@Data
public class AnonymouseReqVo implements Serializable {

    @NotNull(message = "真实用户不能为空")
    private String user;

    @NotNull(message = "帖子ID不能为空")
    private String postId;
}
