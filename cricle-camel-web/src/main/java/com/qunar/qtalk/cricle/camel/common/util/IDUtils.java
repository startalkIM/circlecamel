package com.qunar.qtalk.cricle.camel.common.util;

import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * Created by haoling.wang on 2018/12/29.
 */
public class IDUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getUUIDUpper() {
        return getUUID().toUpperCase();
    }

    public static String generateLikeId(String userId, String objectId) {
        String content = userId + "|" + objectId;
        return DigestUtils.md5DigestAsHex(content.getBytes());
    }

    public static String getUUIDWithType(String type) {
        return String.join("_", type, getUUIDUpper());
    }

    public static void main(String[] args) {
        //String res = generateId("whling", "FB9DDB7A75954401A1D73AA3733C20CC");
        //
        //System.out.println(res);
        //System.out.println(res.length());
        //String s = DigestUtils.md5DigestAsHex("whling|FB9DDB7A75954401A1D73AA3733C20CffffdsafsdfasdfasdfsaC".getBytes());
        //System.out.println(s.toUpperCase());
        //System.out.println(s.length());
        //
        //System.out.println(AESedeUtils.decrypt(res,ContextConsts.SYSTEM_NAME));
    }

}
