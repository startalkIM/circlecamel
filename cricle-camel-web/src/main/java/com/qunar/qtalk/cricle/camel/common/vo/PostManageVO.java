package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PostManageVO implements Serializable {

    @NotNull(message = "请求的帖子编号不能为空")
    private String postUUID;
}
