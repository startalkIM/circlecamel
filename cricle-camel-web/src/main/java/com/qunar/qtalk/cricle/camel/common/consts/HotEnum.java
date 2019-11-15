package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/3/1.
 */
public enum HotEnum {

    IS_HOT(1, true, "置热帖"),
    IS_NOT_HOT(0, false, "非置热帖")
    ;



    private int type;

    private boolean sign;

    private String desc;

    public static HotEnum signOf(boolean sign) {
        return Arrays.stream(values())
                .filter(hotEnum -> hotEnum.sign == sign)
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

    HotEnum(int type, boolean sign, String desc) {
        this.type = type;
        this.sign = sign;
        this.desc = desc;
    }
}
