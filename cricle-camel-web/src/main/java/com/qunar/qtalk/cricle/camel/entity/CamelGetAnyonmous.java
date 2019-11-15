package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CamelGetAnyonmous extends CamelAnonymous {
    private Integer replaceable = 1; //匿名是否可更换 0 不可更换，1可更换的
}
