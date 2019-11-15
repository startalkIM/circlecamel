package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;
import com.qunar.qtalk.cricle.camel.common.exception.MySQLException;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostHistoryByUserVo;
import com.qunar.qtalk.cricle.camel.common.vo.PostHistoryReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.PostReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.PostUploadVo;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.*;

/**
 * PostController
 * 发帖接口
 *
 * @author binz.zhang
 * @date 2018/12/29
 */
@Component
@RestController
@RequestMapping("/newapi/cricle_camel/")
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private CamelPostService camelPostService;

    @PostMapping("/post")
    public JsonResult<?> uploadPOST(@RequestBody CamelPost param) {
        if (null == param) {
            LOGGER.info("/post fail due to illegal param");
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        if (!camelPostService.checkPostContent(param.getContent()) || !camelPostService.checkPostImg(param.getContent())) {
            LOGGER.info("/post fail due to content empty:{}", param.getContent());
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        LOGGER.info("upload new post the post uuid is {}", JacksonUtils.obj2String(param));
        Timestamp now = new Timestamp(System.currentTimeMillis());
        param.setUpdateTime(now);
        param.setCreateTime(now);
        try {
            camelPostService.saveCamelPost(param);
        } catch (DuplicateKeyException e) {
            LOGGER.error("this post already exist :{}", e);
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        } catch (RuntimeException e) {
            LOGGER.error("insert post to camel_post fail; insert value:{};Exception detail:{}", param, e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        return camelPostService.getPostList(new PostReqVo());
    }

    @Deprecated
    @PostMapping("/getNewPost")
    public JsonResult<?> loadPost(@RequestBody String param) {
        JSONObject receivedParam = JSON.parseObject(param);
        Integer iter = (Integer) receivedParam.get("itemNum");
        if (null == iter || iter.equals(0)) {
            iter = 20;
        }
        return getNewestPost(iter);
    }

    @PostMapping("/getPostDetail")
    public JsonResult getPostDetail(@RequestBody String param) {
        LOGGER.info("get post detail the uuid is:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("uuid");
        CamelPostDto camelPostDto;
        try {
            camelPostDto = camelPostService.getPostDetailByUUID(uuid);
        } catch (Exception e) {
            LOGGER.error("select post by post_uuid fail,the reason is {}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        if (null == camelPostDto) {
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND.getCode(), BaseCode.OP_RESOURCE_NOTFOUND.getMsg());
        }

        return JsonResultUtils.success(camelPostDto);
    }

    @PostMapping("/deletePost")
    public JsonResult deletePost(@RequestBody String param) {
        LOGGER.info("delete post the uuid is {}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("uuid");
        if (Strings.isNullOrEmpty(uuid)) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        Integer id;
        try {
            camelPostService.markDeleteFlag(uuid, 1);
            id = camelPostService.selectIdByUid(uuid);
        } catch (Exception e) {
            LOGGER.error("update camel_post delete_flag fail:{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("id", id);
        result.put("isDelete", 1);
        return JsonResultUtils.success(result);
    }

    @Deprecated
    @PostMapping("post/history")
    public JsonResult postHistory(@RequestBody @Valid PostHistoryReqVo postHistoryReqVo) {
        return camelPostService.postHistory(postHistoryReqVo);
    }

    @Deprecated
    @PostMapping("post/getHistoryByUser")
    public JsonResult getHistoryByUser(@RequestBody @Valid PostHistoryByUserVo postHistoryByUserVo) {
        return camelPostService.postHistoryByUser(postHistoryByUserVo);
    }

    public JsonResult getNewestPost(Integer receiveId) throws MySQLException {
        List<CamelPostDto> newPost;
        List<CamelDelete> deletes = null;
        try {
            newPost = camelPostService.selectNewPost(receiveId);
            if (newPost != null && newPost.size() > 1) {
                int startId = newPost.get(newPost.size() - 1).getId();
                deletes = camelPostService.selectDeletePostById(startId, -1);
            }
        } catch (RuntimeException e) {
            LOGGER.error("select camel_post table fail,exception:{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("newPost", newPost);
        result.put("deletePost", deletes);
        return JsonResultUtils.success(result);
    }

    @PostMapping("post/getPostList")
    public JsonResult getPostList(@RequestBody PostReqVo postReqVo) {
        LOGGER.info("getPostList the param is {}", JacksonUtils.obj2String(postReqVo));
        return camelPostService.getPostList(postReqVo);
    }

    @PostMapping("/post/V2")
    public JsonResult<?> uploadPOSTV2(@RequestBody PostUploadVo param) {
        if (null == param) {
            LOGGER.info("/post/V2 fail due to illegal param");
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        if (!camelPostService.checkPostContent(param.getContent()) || !camelPostService.checkPostImg(param.getContent())) {
            LOGGER.info("/post fail due to content empty:{}", param.getContent());
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        camelPostService.checkShareTitle(param);
        LOGGER.info("upload new post the post uuid is {}", JacksonUtils.obj2String(param));
        Timestamp now = new Timestamp(System.currentTimeMillis());
        param.setUpdateTime(now);
        param.setCreateTime(now);
        try {
            camelPostService.saveCamelPost(param);
        } catch (DuplicateKeyException e) {
            LOGGER.error("this post already exist :{}", e);
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        } catch (RuntimeException e) {
            LOGGER.error("insert post to camel_post fail; insert value:{};Exception detail:{}", param, e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        PostReqVo postReqVo = new PostReqVo();
        postReqVo.setPostType(param.getPostType() != null ? param.getPostType() : 7);
        return camelPostService.getPostListAttachHotComment(postReqVo);
    }

    /**
     * v2版本客户端获取帖子列表带上最热评论
     *
     * @param postReqVo
     * @return
     */
    @PostMapping("post/getPostList/v2")
    public JsonResult getWithHotComment(@RequestBody PostReqVo postReqVo) {
        LOGGER.info("getPostList/v2 the param is {}", JacksonUtils.obj2String(postReqVo));
        return camelPostService.getPostListAttachHotComment(postReqVo);
    }

    /**
     * @param
     * @return
     */
    @PostMapping("/fixBeforePost")
    public JsonResult fixBeforeData(@RequestBody String param) {
        LOGGER.info("delete post the uuid is {}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        Integer beginID = (Integer) receivedParam.get("beginID");
        Integer endId = (Integer) receivedParam.get("endID");
        return JsonResultUtils.success(camelPostService.fixSearchContent(beginID, endId));
    }
}
