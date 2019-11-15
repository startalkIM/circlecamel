package com.qunar.qtalk.cricle.camel.web.controller;

import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.dto.CamelAuthInfo;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.service.CamelAuthService;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CricleEntrance
 * @author binz.zhang
 * @date 2019/1/14
 */
@RestController
@RequestMapping("/newapi/cricle_camel/")
public class CricleEntrance {
    private static final Logger LOGGER = LoggerFactory.getLogger(CricleEntrance.class);
    public static final String HOST = "ejabhost1";

    @Autowired
    private CamelAuthService camelAuthService;
    @Autowired
    private CamelPostService camelPostService;



    @GetMapping("/entrance")
    public JsonResult<?> entranceCheck() {
        Integer hostId;
        String userName = UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME);
        String host = UserHolder.getValue(CookieAuthUtils.KEY_USER_DOMAIN);
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(host)) {
            LOGGER.warn("q_ckey 为空！");
            return JsonResultUtils.fail(0, "permission denied");
        }
        if (host.equals(HOST)) {
            hostId = 1;
        } else {
            hostId = 2;
        }
        if (camelAuthService.authUser(userName, hostId)) {
            return JsonResultUtils.success();
        }
        return JsonResultUtils.fail(0, "permission denied");
    }

    @GetMapping("/entranceV2")
    public JsonResult<?> entranceCheckV2() {
        Integer hostId;
        String userName = UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME);
        String host = UserHolder.getValue(CookieAuthUtils.KEY_USER_DOMAIN);
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(host)) {
            LOGGER.warn("q_ckey 为空！");
            return JsonResultUtils.fail(BaseCode.NO_PERMISSION, CamelAuthInfo.builder().authSign(false).build());
        }
        if (host.equals(HOST)) {
            hostId = 1;
        } else {
            hostId = 2;
        }
        if (camelAuthService.authUser(userName, hostId)) {
            return JsonResultUtils.success(CamelAuthInfo.builder().authSign(true).build());
        }
        return JsonResultUtils.success(CamelAuthInfo.builder().authSign(false).build());
    }
}
