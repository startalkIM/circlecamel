package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @我的列表返回值
 */
@Data
public class AtResultDto implements Serializable {

    private List<Map> newAtList = Lists.newArrayList();

    private List<CamelMessageDeleteDto> deleteAtList = Lists.newArrayList();
}
