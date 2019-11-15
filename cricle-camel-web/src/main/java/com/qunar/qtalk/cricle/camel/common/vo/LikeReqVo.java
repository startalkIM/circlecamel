package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Data
public class LikeReqVo implements Serializable {

    @NotEmpty(message = "操作人不能为空")
    private String userId;

    @NotEmpty(message = "操作人所在域不能为空")
    private String userHost;

    @NotNull(message = "操作类型不能为空")
    private Integer opType;

    @NotNull(message = "点赞状态不能为空")
    private Integer likeType;

    private String postId;

    private String commentId;

    @NotEmpty(message = "点赞ID不能为空")
    private String likeId;

    /**
     * 2.0新加字段，点赞评论时需上传该内容
     */
    private String superParentUUID;

    private Integer attachCommentCount = 5;

}
