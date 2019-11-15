package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.MessageReqVo;
import com.qunar.qtalk.cricle.camel.service.CamelMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;


/**
 * Created by haoling.wang on 2019/1/17.
 */
@RestController
@RequestMapping("newapi/cricle_camel/message/")
public class CamelMessageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelMessageController.class);

    @Resource
    private CamelMessageService camelMessageService;

    @PostMapping("/readMark")
    public JsonResult msgReadMark(@RequestBody String param) {
        LOGGER.info("readMark the param is:{}",param);
        JSONObject receivedParam = JSON.parseObject(param);
        Timestamp cliTime = new Timestamp((long) receivedParam.get("time"));
        String userName = UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME);
        String host = UserHolder.getValue(CookieAuthUtils.KEY_USER_DOMAIN);
        try {
            camelMessageService.updateReadFlagByTime(cliTime, userName, host);
        } catch (Exception e) {
            LOGGER.error("update the read flag of the camel_message fail;{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), "update the read flag of the camel_message fail");
        }
        return JsonResultUtils.success();
    }

    @PostMapping("getMessageList")
    public JsonResult getMessageList(@RequestBody @Valid MessageReqVo messageReqVo) {
        return camelMessageService.getMessageList(messageReqVo);
    }

}
