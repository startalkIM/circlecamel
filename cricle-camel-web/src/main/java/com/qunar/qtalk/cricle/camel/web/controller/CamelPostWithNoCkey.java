package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.dto.ValidateMacTokenResult;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.REQUEST_POST;
import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.REQUEST_POST_UPLOAD;

@RestController
@RequestMapping("/newapi/cricle_camel/nck/")
public class CamelPostWithNoCkey {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelPostWithNoCkey.class);

    @Autowired
    private CamelPostService camelPostService;

    @Autowired
    private PostController postController;

    @PostMapping("/post")
    public JsonResult<?> uploadPOST(@RequestBody CamelPost param,HttpServletRequest servletRequest) {
        //@RequestBody CamelPost param,
        ValidateMacTokenResult validateMacTokenResult = camelPostService.validateMacToken(servletRequest);
        if (!validateMacTokenResult.isValidate()) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), validateMacTokenResult.getValidateMsg());
        }
         return postController.uploadPOST(param);
    }
}
