package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.Arrays;

/**
 * Created by haoling.wang on 2019/1/3.
 * <p>
 * 点赞功能操作类型枚举
 */
public enum LikeTypeEnum {

    LIKE(1, true, "点赞"),
    DISLIKE(0, false, "取消赞");

    private int type;

    private boolean sign;

    private String desc;

    LikeTypeEnum(int type, boolean sign, String desc) {
        this.type = type;
        this.sign = sign;
        this.desc = desc;
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


    public static LikeTypeEnum typeOf(int type) {
        return Arrays.stream(LikeTypeEnum.values()).filter(likeTypeEnum -> likeTypeEnum.getType() == type).findAny().orElse(null);
    }

    public static LikeTypeEnum signOf(boolean sign) {
        for (LikeTypeEnum likeTypeEnum : values()) {
            if (sign == likeTypeEnum.isSign()) {
                return likeTypeEnum;
            }
        }
        return null;
    }
}
