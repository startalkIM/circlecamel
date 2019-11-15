package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import lombok.Data;

/**
 * Created by haoling.wang on 2019/1/8.
 */
@Data
public class CamelPostDto extends CamelPost {

    private Integer isLike; // 1:点赞 0:未点赞


    /**
     * 后台管理新增改字段
     */
    private String ownerName; //发帖人姓名
}

