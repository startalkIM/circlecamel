package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.common.dto.CamelManageSystemSerchDto;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newapi/cricle_camel/nck")
public class CamelManageSystemController {

    @GetMapping("/serchPost")
    public JsonResult searchPost(@RequestBody CamelManageSystemSerchDto param) {
        return null;
    }

    //TODO 置帖子为热门贴
    public JsonResult setPostHot() {
        return null;
    }

    //TODO 取消置热
    public JsonResult cancelHot() {
        return null;
    }

    //TODO 置帖子为置顶贴
    public JsonResult setPostTop() {
        return null;
    }

    //TODO 取消置顶
    public JsonResult cancelTop() {
        return null;
    }

    //TODO  删除帖子
    public JsonResult deletePost() {
        return null;
    }


}
