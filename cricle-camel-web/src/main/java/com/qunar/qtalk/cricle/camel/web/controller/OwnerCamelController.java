package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.vo.AtReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.ReplyReqVo;
import com.qunar.qtalk.cricle.camel.service.CamelCommentService;
import com.qunar.qtalk.cricle.camel.service.OwnerCamelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("newapi/cricle_camel/ownerCamel/")
public class OwnerCamelController {

    @Resource
    private OwnerCamelService ownerCamelService;

    /**
     * 获取"我的回复"列表（帖子回复及评论的回复）
     *
     * @return
     */
    @PostMapping("getMyReply")
    public JsonResult getMyReply(@RequestBody @Valid ReplyReqVo replyReqVo) {
        return ownerCamelService.getReplyWithOwnerInfo(replyReqVo);
    }

    @PostMapping("getAtList")
    public JsonResult getAtList(@RequestBody @Valid AtReqVo atReqVo){
        return ownerCamelService.getAtListWithOwnerInfo(atReqVo);
    }

    public static void main(String[] args) {
      System.out.println();
    }
}
