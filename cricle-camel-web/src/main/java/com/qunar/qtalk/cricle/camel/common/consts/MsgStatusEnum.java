package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/1/16.
 */
public enum MsgStatusEnum {

    UNREAD(0, "未读"),
    READ(1, "已读"),
    DELETE(2,"删除")
    ;

    private int code;

    private String desc;

    MsgStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MsgStatusEnum codeOf(int code) {
        return Arrays.stream(values()).filter(msgStatusEnum -> msgStatusEnum.getCode() == code)
                .findFirst().orElse(null);
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
