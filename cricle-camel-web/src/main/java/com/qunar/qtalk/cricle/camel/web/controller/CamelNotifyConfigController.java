package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.vo.NotifyConfigQueryVo;
import com.qunar.qtalk.cricle.camel.service.CamelNotifyConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/newapi/cricle_camel/notify_config")
public class CamelNotifyConfigController {

    @Resource
    private CamelNotifyConfigService camelNotifyConfigService;

    @PostMapping("/getNotifyConfig")
    public JsonResult getNotifyConfig(@Valid @RequestBody NotifyConfigQueryVo notifyConfigQueryVo) {
        return camelNotifyConfigService.getNotifyConfigByUserInfo(notifyConfigQueryVo);
    }

    @PostMapping("/updateNotifyConfig")
    public JsonResult updateNotifyConfig(@Valid @RequestBody NotifyConfigQueryVo notifyConfigQueryVo) {
        return camelNotifyConfigService.updateNotifyConfig(notifyConfigQueryVo);
    }
}
