package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class AtMessageQueryDto extends QueryDto {

    private List<Integer> eventType;

    /**
     * message表中的主键
     */
    private Integer id;
}
