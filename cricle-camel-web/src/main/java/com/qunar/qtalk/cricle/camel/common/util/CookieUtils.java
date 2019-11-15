package com.qunar.qtalk.cricle.camel.common.util;

import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class CookieUtils {

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies;
        if (ArrayUtils.isNotEmpty(cookies = request.getCookies())) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie;
        if (null != (cookie = getCookie(request, name))) {
            return cookie.getValue();
        }
        return null;
    }
}
