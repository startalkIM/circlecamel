package com.qunar.qtalk.cricle.camel.common.consts;

/**
 * 回复类型
 */
public enum ReplyEnum {
    POST(0, "对帖子回复"),
    COMMENT(1, "对评论回复")
    ;

    public int code;

    public String desc;

    ReplyEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
