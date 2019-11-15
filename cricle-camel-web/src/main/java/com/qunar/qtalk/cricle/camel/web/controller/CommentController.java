package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.RedisConsts;
import com.qunar.qtalk.cricle.camel.common.dto.*;
import com.qunar.qtalk.cricle.camel.common.exception.MySQLException;
import com.qunar.qtalk.cricle.camel.common.exception.RedisOpsException;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.ReplyReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.service.CamelCommentService;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.*;

/**
 * CommentController
 *
 * @author binz.zhang
 * @date 2019/1/3
 */
@RestController
@RequestMapping("/newapi/cricle_camel/")
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    public static final Integer HOT_COMMENTS = 3;
    @Resource
    private CamelCommentService camelCommentService;
    @Resource
    private CamelPostService camelPostService;

    @Resource
    private CamelLikeService camelLikeService;

    @PostMapping("/uploadComment")
    public JsonResult<?> uploadComment(@RequestBody CamelComment param) {
        LOGGER.info("upload new comment the post uuid is {},the comment uuid is{}", param.getPostUUID(),
                param.getCommentUUID());
        if(!camelCommentService.checkEmptyContent(param.getContent())){
            LOGGER.warn("upload comment fail due to content is empty:{}",param.getContent());
           return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(),BaseCode.BADREQUEST.getMsg());
        }
        camelCommentService.getTheSuperParentUUID(param);
        LOGGER.info("/uploadComment param:{}", JSON.toJSONString(param));
        JsonResult result = camelCommentService.uploadComment(param);
        if (!result.isRet()) {
            return result;
        }
        try {
            Integer currentId = camelCommentService.selectMaxIdByUUID(param.getPostUUID());
            return camelCommentService.selectHistoryComments(currentId + 1, 20, param.getPostUUID());
        } catch (MySQLException e) {
            LOGGER.error("select comments history fail.", e);
        } catch (RedisOpsException e) {
            LOGGER.error("upload comment occur redis exception.", e);
        } catch (Exception e) {
            LOGGER.error("upload comment occur unknown exception.", e);
        }
        //查询失败 只返回当前的插入的帖子
        List<CamelComment> camelComments = new ArrayList<>();
        camelComments.add(param);
        List<CamelCommentDto> dto = camelCommentService.checkCurUserLikeInfo(camelComments);
        return JsonResultUtils.success(dto);
    }

    @PostMapping("/getHotComment")
    public JsonResult<?> getHotComment(@RequestBody String param) {
        LOGGER.info("/getHotComment param:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("uuid");
        Integer item = (Integer) receivedParam.get("itemNum");
        if (null == item || item < 0) {
            item = HOT_COMMENTS;
        }
        Assert.assertArgNotNull(uuid, "getHotComment fail due to uuid is empty");
        List<CamelCommentDto> hotComments;
        try {
            hotComments = camelCommentService.selectHotComments(item, uuid);
        } catch (RuntimeException e) {
            LOGGER.error("select hot comments fail :{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        return JsonResultUtils.success(hotComments);
    }
    @PostMapping("/getHotComment/V2")
    public JsonResult<?> getHotCommentV2(@RequestBody String param) {
        LOGGER.info("/getHotCommentV2 param:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("uuid");
        Integer item = (Integer) receivedParam.get("itemNum");
        //热门评论的数量2.0 写死3条
        item = HOT_COMMENTS;
        Assert.assertArgNotNull(uuid, "getHotCommentV2 fail due to uuid is empty");
        try {
            return camelCommentService.selectHotCommentsV2(item, uuid);
        } catch (RuntimeException e) {
            LOGGER.error("select hot comments fail :{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
    }

    @PostMapping("/getNewComment")
    public JsonResult<?> getNewComment(@RequestBody String param) {
        LOGGER.info("/getNewComment param:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("postUUID");
        Integer item = (Integer) receivedParam.get("pgSize");
        if (null == item || item < 0) {
            item = 20;
        }
        Assert.assertArgNotNull(uuid, "getNewComment fail due to uuid is empty");
        Integer currentId = 0;
        try {
            currentId = camelCommentService.selectMaxIdByUUID(uuid);
        } catch (RuntimeException e) {
            LOGGER.error("select the comment max id by post_uuid fail:{}", e);
        }
        if (currentId == null) {
            currentId = 0;
        }
        try {
            return camelCommentService.selectHistoryComments(currentId + 1, item, uuid);
        } catch (RuntimeException e) {
            LOGGER.error("select new comments fail :{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
    }

    @PostMapping("/getNewComment/V2")
    public JsonResult<?> getNewCommentV2(@RequestBody String param) {
        LOGGER.info("/getNewComment/V2 param:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        String uuid = (String) receivedParam.get("postUUID");
        Integer item = (Integer) receivedParam.get("pgSize");
        List<String> hotcommentuuid = (List) receivedParam.get("hotCommentUUID");
        Integer attachItem = (Integer) receivedParam.get("attachCommentCount");
        if (attachItem == null) {
            attachItem = 5;
        }
        if (null == item || item < 0) {
            item = 20;
        }
        Assert.assertArgNotNull(uuid, "getNewCommentV2 fail due to uuid is empty");
        Integer currentId = 0;
        try {
            currentId = camelCommentService.selectMaxIdByUUID(uuid);
        } catch (RuntimeException e) {
            LOGGER.error("select the comment max id by post_uuid fail:{}", e);
        }
        if (currentId == null) {
            currentId = 0;
        }
        //todo check the case of commentResultModel
        CommentResultModel commentResultModel;
        try {
            commentResultModel = camelCommentService.getTheCommentsVersion2(currentId + 1, item, uuid, hotcommentuuid);
            commentResultModel.setAttachCommentList(camelCommentService.getPostAttachHotComment(uuid,attachItem));
        } catch (RuntimeException e) {
            LOGGER.error("select new comments fail :{}", e);
            return JsonResultUtils.fail(BaseCode.ERROR.getCode(), BaseCode.ERROR.getMsg());
        }
        return JsonResultUtils.success(commentResultModel);
    }


    @PostMapping("/getHistoryComment")
    public JsonResult<?> getHistoryComment(@RequestBody String param) {
        LOGGER.info("/getHistoryComment param:{}", param);
        JSONObject receivedParam = JSON.parseObject(param);
        Integer curCommentId = (Integer) receivedParam.get("curCommentId");
        Integer pgSize = (Integer) receivedParam.get("pgSize");
        String uuid = (String) receivedParam.get("postUUID");
        if (Strings.isNullOrEmpty(uuid) || curCommentId == null || curCommentId < 0 || pgSize == null || pgSize < 0) {
            LOGGER.error("select history comments fail due to param is illegal");
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        return camelCommentService.selectHistoryComments(curCommentId, pgSize, uuid);
    }

    @PostMapping("/deleteComment")
    public JsonResult<?> deleteComment(@RequestBody CommentDeleteReqDto param) {
        LOGGER.info("/deleteComment param:{}", param);
        CommentlDeleteResultV1Dto commentlDeleteResultV1Dto = camelCommentService.deleteCommentService(param.getPostUUID(), param.getCommentUUID());
        if (commentlDeleteResultV1Dto == null) {
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        return JsonResultUtils.success(commentlDeleteResultV1Dto);
    }

    @PostMapping("/deleteComment/V2")
    public JsonResult<?> deleteCommentV2(@RequestBody CommentDeleteReqDto param) {
        LOGGER.info("/deleteComment/V2 param:{}", param);
        CommentlDeleteResultV1Dto commentlDeleteResultV1Dto = camelCommentService.deleteCommentService(param.getPostUUID(), param.getCommentUUID());
        CamelComment camelComment;
        CommentDeleteResultDto commentDeleteResultDto = new CommentDeleteResultDto();
        commentDeleteResultDto.setPostUUID(param.getPostUUID());
        commentDeleteResultDto.setCommentUUID(param.getCommentUUID());
        commentDeleteResultDto.setSuperParentCommentUUID(param.getSuperParentUUID());
        String superParentUUID = param.getSuperParentUUID();
        if (Strings.isNullOrEmpty(superParentUUID)) {
            superParentUUID = param.getCommentUUID();
            commentDeleteResultDto.setDeleteType(0);
        } else {
            commentDeleteResultDto.setDeleteType(1);
        }
        camelComment = camelCommentService.getCamelCommentByUUID(superParentUUID);
        commentDeleteResultDto.setSuperParentStatus(camelComment.getCommentStatus());
        commentDeleteResultDto.setIsDelete(1);
        List<CamelCommentDto> attachCommentList = camelCommentService.getPostAttachHotComment(param.getPostUUID(),param.getAttachCommentCount());
        HashMap<String, Object> deleteData = new HashMap<>(3);
        deleteData.put("postCommentNum", commentlDeleteResultV1Dto.getPostCommentNum());
        deleteData.put("postLikeNum", commentlDeleteResultV1Dto.getPostLikeNum());
        deleteData.put("deleteCommentData", commentDeleteResultDto);
        deleteData.put("attachCommentList", attachCommentList);
        return JsonResultUtils.success(deleteData);
    }

    @PostMapping("/uploadComment/V2")
    public JsonResult uploadCommentV2(@RequestBody CamelComment param) {
        LOGGER.info("/uploadComment/V2 param:{}", JacksonUtils.obj2String(param));
        Assert.assertArgNotNull(param, "upload comment is null");
        if(!camelCommentService.checkEmptyContent(param.getContent())){
            LOGGER.warn("upload comment fail due to content is empty:{}",param.getContent());
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(),BaseCode.BADREQUEST.getMsg());
        }
        if (!Strings.isNullOrEmpty(param.getParentCommentUUID())) {
            return camelCommentService.uploadChildComment(param);
        }
        if (Strings.isNullOrEmpty(param.getSuperParentUUID()) && Strings.isNullOrEmpty(param.getParentCommentUUID())) {
            return camelCommentService.uploadParentComment(param, param.getHotCommentUUID());
        }
        return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
    }


    @PostMapping("/getHistoryComment/V2")
    public JsonResult<?> getCommentsV2(@RequestBody String param) {
        JSONObject receivedParam = JSON.parseObject(param);
        Integer curCommentId = (Integer) receivedParam.get("curCommentId");
        Integer pgSize = (Integer) receivedParam.get("pgSize");
        String uuid = (String) receivedParam.get("postUUID");
        List<String> hotcommentuuid = (List) receivedParam.get("hotCommentUUID");
        return JsonResultUtils.success(camelCommentService.getTheCommentsVersion2(curCommentId, pgSize, uuid, hotcommentuuid));
    }

}
