package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class CommentQueryDto implements Serializable {

    private Timestamp commentCreateTime;

    private String owner;

    private String ownerHost;

    private Integer pageSize = 20;
}
