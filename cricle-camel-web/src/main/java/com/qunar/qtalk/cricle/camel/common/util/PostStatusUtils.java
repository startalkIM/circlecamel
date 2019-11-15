package com.qunar.qtalk.cricle.camel.common.util;

/**
 * Created by haoling.wang on 2019/3/5.
 * <p>
 * 帖子状态工具类
 *
 * 0000 0000 -> 0 none
 * 0000 0001 -> 1 normal
 * 0000 0010 -> 2 only top
 * 0000 0100 -> 4 only hot
 * 0000 0011 -> 3 normal、top
 * 0000 0101 -> 5 normal、hot
 * 0000 0110 -> 6 top、hot
 * 0000 0111 -> 7 normal、top、hot
 */
public class PostStatusUtils {

    public static boolean needTop(Integer postStatus) {
        if (postStatus == 2 || postStatus == 3 || postStatus == 6 || postStatus == 7) {
            return true;
        }
        return false;
    }

    public static boolean needHot(Integer postStatus) {
        if (postStatus == 4 || postStatus == 5 || postStatus == 6 || postStatus == 7) {
            return true;
        }
        return false;
    }

    public static boolean needNormal(Integer postStatus) {
        if (postStatus == 1 || postStatus == 3 || postStatus == 5 || postStatus == 7) {
            return true;
        }
        return false;
    }

}
