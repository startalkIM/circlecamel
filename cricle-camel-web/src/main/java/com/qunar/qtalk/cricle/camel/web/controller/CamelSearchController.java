package com.qunar.qtalk.cricle.camel.web.controller;


import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelSearchType;
import com.qunar.qtalk.cricle.camel.service.CamelSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/newapi/cricle_camel/")
public class CamelSearchController {

    @Resource
    private CamelSearchService camelSearchService;


    @RequestMapping(value = "/search")
    public JsonResult search(@RequestBody CamelSearchType param) throws IOException {
        log.info("search the param is {}", JacksonUtils.obj2String(param));
        return camelSearchService.search(param);
    }


}
