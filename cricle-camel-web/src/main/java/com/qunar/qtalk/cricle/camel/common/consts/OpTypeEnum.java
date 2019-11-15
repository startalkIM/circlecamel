package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/1/4.
 * <p>
 * 操作对象类型枚举
 */
public enum OpTypeEnum {
    OP_POST(1, "post", "帖子"),
    OP_COMMENT(2, "comment", "评论");

    private int type;

    private String name;

    private String desc;

    OpTypeEnum(int type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
    }

    public static OpTypeEnum typeOf(int type) {
        return Arrays.stream(OpTypeEnum.values()).filter(likeTypeEnum -> likeTypeEnum.getType() == type).findAny().orElse(null);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
