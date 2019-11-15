package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
public class CamelPost implements Serializable {
    /**
     * null
     */
    private Integer id;

    /**
     * null
     */

    @NotEmpty(message = "uuid 不能为空")
    private String uuid;

    /**
     * null
     */
    @NotEmpty(message = "owner 不能为空")
    private String owner;

    /**
     * null
     */
    @NotEmpty(message = "ownerHost 不能为空")
    private String ownerHost;

    /**
     * null
     */

    private Timestamp createTime;

    /**
     * null
     */
    @NotEmpty(message = "content 不能为空")
    private String content;

    /**
     * null
     */

    private Integer isDelete;

    /**
     * null
     */
    private Timestamp updateTime;

    /**
     * null
     */
    private String atList;

    /**
     * null
     */
    @NotEmpty(message = "isAnonymous 不能为空")
    private Integer isAnonymous;

    /**
     * null
     */
    private Integer likeNum;


    /**
     * null
     */
    private String anonymousName;

    /**
     * null
     */
    private Integer reviewStatus;

    /**
     * null
     */
    private String anonymousPhoto;


    private Integer commentsNum;

    /**
     * 2.0新加字段，标识帖子的类型。N普通帖子；H 热门贴；T 置顶贴...
     */
    private Integer postType;

}