package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 驼圈通知开关
 */
public enum CamelNotifyFlagEnum {

    ON(1),
    OFF(0);

    public int flag;

    CamelNotifyFlagEnum(int flag) {
        this.flag = flag;
    }

    public static CamelNotifyFlagEnum flagOf(Integer flag) {
        return EnumSet.allOf(CamelNotifyFlagEnum.class).stream()
                .filter(x -> Objects.equals(x.flag, flag)).findFirst().orElse(null);
    }

    public boolean isOn() {
        return Objects.equals(this, ON) ? true : false;
    }
}
