package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.EnumSet;
import java.util.Objects;

public enum AnonymousEnum {

    IS_ANONYMOUSE(1, true, "匿名"),
    NO_ANONYMOUSE(0, false, "非匿名")
    ;

    public int code;

    public boolean isAnonymouse;

    public String desc;

    AnonymousEnum(int code, boolean isAnonymouse, String desc) {
        this.code = code;
        this.isAnonymouse = isAnonymouse;
        this.desc = desc;
    }

    public static AnonymousEnum codeOf(int code) {
        return EnumSet.allOf(AnonymousEnum.class)
                .stream().filter(anonymousEnum -> Objects.equals(anonymousEnum.code, code))
                .findFirst().orElse(null);
    }
}
