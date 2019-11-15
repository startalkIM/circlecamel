package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/3/1.
 */
public enum TopEnum {

    IS_TOP(1, true, "置顶帖"),
    IS_NOT_TOP(0, false, "非置顶帖")
    ;

    private int type;

    private boolean sign;

    private String desc;

    TopEnum(int type, boolean sign, String desc) {
        this.type = type;
        this.sign = sign;
        this.desc = desc;
    }

    public static TopEnum signOf(boolean sign) {
        return Arrays.stream(values())
                .filter(topEnum -> topEnum.sign == sign)
                .findFirst().orElse(null);
    }

    public int getType() {
        return type;
    }

    public boolean isSign() {
        return sign;
    }

    public String getDesc() {
        return desc;
    }
}
