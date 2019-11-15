package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/1/9.
 */
public enum DeleteEnum {

    NO_DELETED(0, false, "未删除"),
    DELETED(1, true, "已删除")
    ;


    private int code;

    private boolean status;

    private String desc;

    DeleteEnum(int code, boolean status, String desc) {
        this.code = code;
        this.status = status;
        this.desc = desc;
    }

    public static DeleteEnum codeOf(int code) {
        return Arrays.stream(DeleteEnum.values())
                .filter(deleteEnum -> deleteEnum.getCode() == code)
                .findFirst().orElse(null);
    }

    public static DeleteEnum statusOf(boolean status) {
        return Arrays.stream(DeleteEnum.values())
                .filter(deleteEnum -> deleteEnum.isStatus() == status)
                .findFirst().orElse(null);
    }

    public int getCode() {
        return code;
    }

    public boolean isStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
