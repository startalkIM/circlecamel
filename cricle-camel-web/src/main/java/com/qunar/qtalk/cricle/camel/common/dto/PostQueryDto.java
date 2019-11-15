package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/21.
 */
@Data
public class PostQueryDto implements Serializable {

    private Timestamp postCreateTime;

    private String owner;

    private String ownerHost;

    private Integer pageSize = 20;

    private List<String> excludePostList = Lists.newArrayList();
}
