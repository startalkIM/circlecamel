package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.dto.LikeDto;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.vo.LikeReqVo;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@RestController
@RequestMapping("/newapi/cricle_camel/like")
public class CamelLikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelLikeController.class);

    @Resource
    private CamelLikeService camelLikeService;

    @Resource
    private DozerUtils dozerUtils;

    @PostMapping()
    public JsonResult like(@RequestBody @Valid LikeReqVo likeReqVo) {
        LOGGER.info("/newapi/cricle_camel/like the param is:{}", JacksonUtils.obj2String(likeReqVo));
        LikeDto likeDto = dozerUtils.map(likeReqVo, LikeDto.class);

        return camelLikeService.opLike(likeDto);
    }
}
