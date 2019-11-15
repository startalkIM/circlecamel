package com.qunar.qtalk.cricle.camel.common.schedule;

/**
 * Created by haoling.wang on 2019/1/10.
 */
public enum TaskStatusEnum {

    NO_START(0, "未开始"),
    EXECUTING(1,"执行中"),
    SUCCESS(2,  "执行成功"),
    FAIL(3,     "执行失败")
    ;

    private int code;

    private String desc;

    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskStatusEnum resultOf(boolean result) {
        return result ? SUCCESS : FAIL;
    }
}
