package com.qunar.qtalk.cricle.camel.common.vo;

import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import lombok.Data;

@Data
public class PostUploadVo extends CamelPost {

    private Integer postType;
}
