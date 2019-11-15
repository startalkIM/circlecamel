package com.qunar.qtalk.cricle.camel.common.consts;

/**
 * Created by haoling.wang on 2019/3/7.
 */
public enum PostTypeEnum {

    /**
     * 0000 0000 -> 0 none
     * 0000 0001 -> 1 normal
     * 0000 0010 -> 2 only top
     * 0000 0100 -> 4 only hot
     * 0000 0011 -> 3 normal、top
     * 0000 0101 -> 5 normal、hot
     * 0000 0110 -> 6 top、hot
     * 0000 0111 -> 7 normal、top、hot
     */

    NONE(0, "无状态"),
    NORMAL(1, "正常帖"),
    ONLY_TOP(2, "置顶帖"),
    NORMAL_AND_TOP(3, "正常帖、置顶帖"),
    ONLY_HOT(4, "置热帖"),
    NORMAL_AND_HOT(5, "正常帖、置热帖"),
    TOP_AND_HOT(6, "置顶帖、置热帖"),
    NORMAL_AND_TOP_AND_HOT(7, "正常帖、置顶帖、置热帖")
    ;


    private Integer type;

    private String desc;

    PostTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
