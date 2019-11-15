package com.qunar.qtalk.cricle.camel.web.interceptor;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils;
import com.qunar.qtalk.cricle.camel.common.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils.KEY_USER_DOMAIN;
import static com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils.KEY_USER_NAME;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Slf4j
@CustomInterceptor(order = 2, addPathPatterns = {"/**"})
public class CookieInterceptor implements HandlerInterceptor {

    public static final String COOKIE_CKEY_NAME = "q_ckey";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        try {
            String cKey = CookieUtils.getCookieValue(request, COOKIE_CKEY_NAME);
            if (Strings.isNullOrEmpty(cKey)) {
                log.warn("request not attach user cKey info");
                Map<String,String> defaultUserMap = Maps.newHashMap();
                defaultUserMap.put(KEY_USER_NAME, "System_Default");
                defaultUserMap.put(KEY_USER_DOMAIN, "ejabhost1");
                UserHolder.set(defaultUserMap);
            } else {
                Map<String, String> userInfoMap = CookieAuthUtils.getUserFromCKey(cKey);
                if (MapUtils.isNotEmpty(userInfoMap)) {
                    UserHolder.set(userInfoMap);
                    MDC.put(ContextConsts.MDC_USER, userInfoMap.get(KEY_USER_NAME));
                }
            }
        } catch (Exception e) {
            log.error("parse cookie info occur exception", e);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        UserHolder.remove();
    }

}
