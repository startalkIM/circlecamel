package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Data
public class QueryDto implements Serializable {

    private Timestamp createTime;

    private String owner;

    private String ownerHost;

    private Integer pageSize = 20;

}
