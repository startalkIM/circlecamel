package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/2/27.
 */
public enum ManageOpType {

    POST_SET_TOP(1001, "置顶贴子"),
    POST_NOT_SET_TOP(1002, "取消置顶帖子"),
    POST_SET_HOT(1003, "置热帖子"),
    POST_NOT_SET_HOT(1004, "取消置热帖子"),
    POST_DELETE(1005, "删除帖子"),


    ;
    private int opCode;

    private String desc;

    ManageOpType(int opCode, String desc) {
        this.opCode = opCode;
        this.desc = desc;
    }

    public int getOpCode() {
        return opCode;
    }

    public String getDesc() {
        return desc;
    }

    public static ManageOpType opCodeOf(int opCode) {
        return Arrays.stream(values())
                .filter(manageOpType -> manageOpType.getOpCode() == opCode)
                .findFirst().orElse(null);
    }
}
