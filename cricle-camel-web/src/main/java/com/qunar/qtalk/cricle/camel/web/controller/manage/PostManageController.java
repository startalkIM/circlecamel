package com.qunar.qtalk.cricle.camel.web.controller.manage;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.DeleteEnum;
import com.qunar.qtalk.cricle.camel.common.consts.ManageOpType;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostManageSearchVo;
import com.qunar.qtalk.cricle.camel.common.vo.PostManageTopHotVo;
import com.qunar.qtalk.cricle.camel.common.vo.PostManageVO;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * Created by haoling.wang on 2019/2/27.
 */
@Slf4j
@RequestMapping("/cricle_camel/nck/manage/post")
@RestController
public class PostManageController {

    private static Integer MAX_TOPORHOT_TIME = 72; //最长置顶或置热36小时

    @Resource
    private CamelPostService postService;

    /**
     * 后台管理-帖子搜索
     *
     * @param postManageSearchVo
     * @return
     */
    @PostMapping("search")
    public JsonResult search(@Valid @RequestBody PostManageSearchVo postManageSearchVo) {
        return postService.search(postManageSearchVo);
    }


    /**
     * 置顶/热
     */
    @PostMapping("setTopOrHot")
    public JsonResult setTopOrHot(@Valid @RequestBody PostManageTopHotVo postManageTopHotVo) {
        log.info("setTopOrHot postManageTopHotVo:{}", JSON.toJSONString(postManageTopHotVo));
        String postUUID = postManageTopHotVo.getPostUUID();
        Integer existTime = postManageTopHotVo.getExistTime(); // 维持的时间,小时为单位
        ManageOpType opType = postManageTopHotVo.opType();
        return postService.setTopOrHotPostService(postUUID, existTime, opType);
    }

    /**
     * 取消置顶/热
     *
     * @param postManageTopHotVo
     * @return
     */
    @PostMapping("deleteTopOrHot")
    public JsonResult deleteTopOrHot(@Valid @RequestBody PostManageTopHotVo postManageTopHotVo) {
        log.info("deleteTopOrHot postManageTopHotVo:{}", JSON.toJSONString(postManageTopHotVo));
        String postUUID = postManageTopHotVo.getPostUUID();
        ManageOpType opType = postManageTopHotVo.opType();
        return postService.deleteTopOrHotPostService(postUUID, opType);
    }

    /**
     * 删除
     */
    @PostMapping("delete")
    public JsonResult delete(@Valid @RequestBody PostManageTopHotVo postManageTopHotVo) {
        log.info("delete postManageTopHotVo:{}", JSON.toJSONString(postManageTopHotVo));
        String postUUID = postManageTopHotVo.getPostUUID();
        ManageOpType opType = postManageTopHotVo.opType();
        if (opType != ManageOpType.POST_DELETE) {
            return JsonResultUtils.fail(BaseCode.OP_NOT_SUPPORT);
        }
        return postService.markDeleteFlag(postUUID, DeleteEnum.DELETED.getCode());
    }

    /**
     * 手动设置最大顶、热帖个数
     *
     * @return
     */
    @GetMapping("setmaxTopOrHotCount/{count}")
    public JsonResult setmaxTopOrHotCount(@PathVariable("count") Integer count) {
        log.info("setmaxTopOrHotCount count:{}", count);
        return postService.setNewMaxCountTopOrHot(count);
    }

    /**
     * 帖子详情
     *
     * @param postManageVO
     * @return
     */
    @PostMapping("postDetail")
    public JsonResult postDetail(@RequestBody PostManageVO postManageVO) {
        log.info("postDetail arg:{}", postManageVO);
        return postService.postDetailForManagement(postManageVO);
    }
}
