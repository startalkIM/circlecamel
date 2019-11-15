package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息删除dto
 */
@Data
public class CamelMessageDeleteDto implements Serializable {

    private String uuid;

    private Integer id;

    private Integer isDelete;
}
