package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * "@我的"列表返回值
 */
@Data
public class AtOwnerResultDto implements Serializable {

    private Date createTime;

    private Map data;
}
