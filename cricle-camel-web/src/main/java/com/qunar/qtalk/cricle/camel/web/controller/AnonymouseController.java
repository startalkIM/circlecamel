package com.qunar.qtalk.cricle.camel.web.controller;

import com.google.common.base.Splitter;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.AnonymouseReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelAnonymous;
import com.qunar.qtalk.cricle.camel.entity.CamelGetAnyonmous;
import com.qunar.qtalk.cricle.camel.service.CamelAnonymousService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Created by haoling.wang on 2019/1/2.
 */
@RestController
@RequestMapping("newapi/cricle_camel/")
public class AnonymouseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnonymouseController.class);

    @Resource
    private CamelAnonymousService camelAnonymousService;
    @Resource
    private DozerUtils dozerUtils;

    @PostMapping(value = {"/anonymouse/getAnonymouse", "/nck/anonymouse/getAnonymouse"})
    public JsonResult getAnonymouseNew(@RequestBody @Valid AnonymouseReqVo anonymouseReqVo) {
        LOGGER.info("getAnonymouse the param is:{}", JacksonUtils.obj2String(anonymouseReqVo));
        List<String> user = Splitter.on("@").splitToList(anonymouseReqVo.getUser());
        anonymouseReqVo.setUser(user.get(0));
        CamelAnonymous camelAnonymous = camelAnonymousService.validateAnyonmous(anonymouseReqVo);
        if (camelAnonymous == null) {
            String uuid = UUID.randomUUID().toString();
            CamelAnonymous camelAnonymousTemp = camelAnonymousService.generateAnonymousName(anonymouseReqVo.getUser(), uuid);
            return JsonResultUtils.success(dozerUtils.map(camelAnonymousTemp, CamelGetAnyonmous.class));
        }
        CamelGetAnyonmous camelGetAnyonmous = dozerUtils.map(camelAnonymous, CamelGetAnyonmous.class);
        camelGetAnyonmous.setReplaceable(0);
        return JsonResultUtils.success(camelGetAnyonmous);
    }


}
