package com.qunar.qtalk.cricle.camel.common.consts;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;

@Getter
public enum PostOrderEnum {

    ASC(1, "升序"),
    DESC(2, "倒序")
    ;

    public int type;

    public String desc;



    public static PostOrderEnum typeOf(int type) {
        return EnumSet.allOf(PostOrderEnum.class)
                .stream().filter(orderEnum -> Objects.equals(type, orderEnum.type))
                .findFirst().orElse(null);
    }

    PostOrderEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
