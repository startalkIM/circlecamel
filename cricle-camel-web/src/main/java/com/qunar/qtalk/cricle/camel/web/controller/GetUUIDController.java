package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;

/**
 * GetUUIDController
 *
 * @author binz.zhang
 * @date 2018/12/29
 */
@RestController(value = "/api/get/")
@RequestMapping("/test")
public class GetUUIDController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetUUIDController.class);

    @Resource
    private CamelPostService camelPostService;

    @GetMapping("getUUID/")
    public JsonResult<?> getUUID(@RequestBody String param) {
        JSONObject receiveParam;
        String receiveT;
        try {
            receiveParam = JSON.parseObject(param);
            receiveT = (String) receiveParam.get("type");
        } catch (RuntimeException e) {
            LOGGER.error("请求类型Json异常>>>，请求参数:{}", param);
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        Random random = new Random();
        String timesTamp = String.valueOf(System.currentTimeMillis());
        int num = random.nextInt(9999);
        String randomNum = String.format("%05d", num);
        StringBuffer sb = new StringBuffer();
        String UUID = sb.append(receiveT).append(timesTamp).append(randomNum).toString();
        return JsonResultUtils.success(UUID);
    }


}
