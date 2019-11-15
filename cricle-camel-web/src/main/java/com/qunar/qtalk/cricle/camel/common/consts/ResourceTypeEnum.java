package com.qunar.qtalk.cricle.camel.common.consts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  ResourceTypeEnum {

    POST("P"),
    COMMENT("C"),
    LIKE("L"),
    PHOTO("PH"),
    VIDEO("V");


    public final String type;
}
