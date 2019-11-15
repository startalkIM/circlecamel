package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.common.consts.DeleteEnum;
import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import com.qunar.qtalk.cricle.camel.common.consts.ReplyEnum;
import com.qunar.qtalk.cricle.camel.common.dto.*;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.Assert;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.AtReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.BaseReqVo;
import com.qunar.qtalk.cricle.camel.common.vo.ReplyReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OwnerCamelService {

    @Resource
    private DozerUtils dozerUtils;

    @Resource
    private CamelPostService camelPostService;

    @Resource
    private CamelCommentService camelCommentService;

    @Resource
    private CamelCommentMapper camelCommentMapper;

    @Resource
    private CamelMessageMapper camelMessageMapper;

    /**
     * 获取'我的回复'列表
     *
     * @param replyReqVo
     * @return
     */
    public JsonResult getReplyWithOwnerInfo(ReplyReqVo replyReqVo) {
        CommentQueryDto commentQueryDto = transReplyVo(replyReqVo);
        ReplyResultDto replyResultDto = new ReplyResultDto();
        try {
            List<CamelComment> camelComments = camelCommentMapper.selectCommentListByQueryDto(commentQueryDto);
            if (CollectionUtils.isNotEmpty(camelComments)) {
                CamelComment camelComment = camelComments.stream().min(Comparator.comparingInt(CamelComment::getId)).get();
                List<CamelComment> commentsListByCreatetime = camelCommentMapper.selectCommentListByCreatetime(commentQueryDto, camelComment.getId(), camelComment.getCreateTime());
                camelComments.addAll(commentsListByCreatetime);
            }
            if (CollectionUtils.isNotEmpty(camelComments)) {
                List<CamelCommentReplyDto> camelCommentReplyDtos = dozerUtils.mapCollection(camelComments, CamelCommentReplyDto.class);
                List<CamelCommentReplyDto> newComment = camelCommentReplyDtos.stream().filter(camelComment -> !DeleteEnum.codeOf(camelComment.getIsDelete()).isStatus())
                        .map(camelCommentReplyDto -> {
                            try {
                                warpCamelCommentReplyDto(camelCommentReplyDto);
                            } catch (Exception e) {
                                log.error("warpCamelCommentReplyDto occur excepiton,camelCommentReplyDto:{}", JSON.toJSONString(camelCommentReplyDto), e);
                            }
                            return camelCommentReplyDto;
                        }).collect(Collectors.toList());
                List<CamelDelete> deleteComment = camelComments.stream().filter(camelComment -> DeleteEnum.codeOf(camelComment.getIsDelete()).isStatus())
                        .map(camelComment -> {
                            CamelDelete camelDelete = new CamelDelete(camelComment.getCommentUUID(), camelComment.getId(), camelComment.getIsDelete());
                            return camelDelete;
                        }).collect(Collectors.toList());
                newComment.sort((o1, o2) -> -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime()));
                replyResultDto.getNewComment().addAll(newComment);
                replyResultDto.getDeleteComments().addAll(deleteComment);
            }
        } catch (Exception e) {
            log.error("getReplyWithOwnerInfo occur exception,replyReqVo:{}", JSON.toJSONString(replyReqVo), e);
        }
        return JsonResultUtils.success(replyResultDto);
    }


    /**
     * 获取'@我的'列表
     *
     * @param atReqVo
     * @return
     */
    public JsonResult getAtListWithOwnerInfo(AtReqVo atReqVo) {
        AtResultDto atResultDto = new AtResultDto();
        try {
            Assert.assertArg(atReqVo != null, "atReqVo is null");
            QueryDto queryDto = transAtReqVo(atReqVo);
            AtMessageQueryDto atMessageQueryDto = dozerUtils.map(queryDto, AtMessageQueryDto.class);
            atMessageQueryDto.setEventType(Lists.newArrayList(EventType.ATREMINDPOST.getType(), EventType.ATREMINDCOMMENT.getType()));
            List<CamelMessage> camelMessages = camelMessageMapper.selectMessageListByQueryDto(atMessageQueryDto);
            if (CollectionUtils.isNotEmpty(camelMessages)) {
                CamelMessage camelMessage = camelMessages.stream().min(Comparator.comparingInt(CamelMessage::getId)).get();
                List<CamelMessage> camelMessagesWithTime = camelMessageMapper.selectMessageListByCreatetime(atMessageQueryDto, camelMessage.getId(), camelMessage.getCreateTime());
                camelMessages.addAll(camelMessagesWithTime);
            }

            if (CollectionUtils.isNotEmpty(camelMessages)) {
                List<Map> res =
                        camelMessages.stream()
                                .filter(camelMessage -> !Objects.equals(camelMessage.getFlag(), MsgStatusEnum.DELETE))
                                .map(camelMessage -> {
                                    // 这个地方直接返回MAP对象，系统中消息content对象存在多个，避免出现映射问题，直接用map
                                    Map map = JSON.parseObject(camelMessage.getContent(), Map.class);
                                    map.put("createTime",camelMessage.getCreateTime());
                                    return map;
                                }).collect(Collectors.toList());

                List<CamelMessageDeleteDto> messageDeleteList = camelMessages.stream().filter(camelMessage -> Objects.equals(camelMessage.getFlag(), MsgStatusEnum.DELETE))
                        .map(camelMessage -> {
                            CamelMessageDeleteDto camelMessageDeleteDto = new CamelMessageDeleteDto();
                            camelMessageDeleteDto.setId(camelMessage.getId());
                            camelMessageDeleteDto.setUuid(camelMessage.getUuid());
                            camelMessageDeleteDto.setIsDelete(DeleteEnum.DELETED.getCode());
                            return camelMessageDeleteDto;
                        }).collect(Collectors.toList());

                res.sort((o1, o2) -> -Long.compare(((Timestamp) o1.get("createTime")).getTime(),
                        ((Timestamp) o2.get("createTime")).getTime()));
                atResultDto.setNewAtList(res);
                atResultDto.setDeleteAtList(messageDeleteList);
            }
        } catch (Exception e) {
            log.error("getAtListWithOwnerInfo occur exception,atReqVo:{}", JSON.toJSONString(atReqVo), e);
        }
        return JsonResultUtils.success(atResultDto);
    }

    private CommentQueryDto transReplyVo(ReplyReqVo replyReqVo) {
        CommentQueryDto commentQueryDto = new CommentQueryDto();
        if (null != replyReqVo) {
            Long commentCreateTime = replyReqVo.getCreateTime();
            Integer pageSize = replyReqVo.getPageSize();
            if (!Strings.isNullOrEmpty(replyReqVo.getOwner())) {
                commentQueryDto.setOwner(replyReqVo.getOwner());
            }
            if (!Strings.isNullOrEmpty(replyReqVo.getOwnerHost())) {
                commentQueryDto.setOwnerHost(replyReqVo.getOwnerHost());
            }
            if (commentCreateTime != null && commentCreateTime != 0L) {
                commentQueryDto.setCommentCreateTime(new Timestamp(commentCreateTime));
            }
            if (pageSize != null && pageSize > 0) {
                commentQueryDto.setPageSize(pageSize);
            }
        }
        return commentQueryDto;
    }

    private QueryDto transAtReqVo(BaseReqVo baseReqVo) {
        QueryDto queryDto = new QueryDto();
        if (null != baseReqVo) {
            Long createTime = baseReqVo.getCreateTime();
            Integer pageSize = baseReqVo.getPageSize();
            if (!Strings.isNullOrEmpty(baseReqVo.getOwner())) {
                queryDto.setOwner(baseReqVo.getOwner());
            }
            if (!Strings.isNullOrEmpty(baseReqVo.getOwnerHost())) {
                queryDto.setOwnerHost(baseReqVo.getOwnerHost());
            }
            if (createTime != null && createTime != 0L) {
                queryDto.setCreateTime(new Timestamp(createTime));
            }
            if (pageSize != null && pageSize > 0) {
                queryDto.setPageSize(pageSize);
            }
        }
        return queryDto;
    }

    private void warpCamelCommentReplyDto(CamelCommentReplyDto camelCommentReplyDto) {
        Integer replyType;
//        CamelComment camelComment = null;
        if (Strings.isNullOrEmpty(camelCommentReplyDto.getParentCommentUUID())) {
            replyType = ReplyEnum.POST.code;
        } else {
            replyType = ReplyEnum.COMMENT.code;
//            camelComment = camelCommentService.getCamelCommentByUUID(camelCommentReplyDto.getCommentUUID());
//            if (DeleteEnum.codeOf(camelComment.getIsDelete()).isStatus()) {
//                camelComment = new CamelComment();
//            }
        }
        CamelPost camelPost  = camelPostService.getCamelPost(camelCommentReplyDto.getPostUUID());
        if (DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()) {
            camelPost = new CamelPost();
        }
        camelCommentReplyDto.setReplyType(replyType);
        camelCommentReplyDto.setCamelPost(camelPost);
//        camelCommentReplyDto.setCamelComment(camelComment);

        // 我的回复 的eventType
        camelCommentReplyDto.setEventType(EventType.MYREPLY.getType());
        camelCommentReplyDto.setUuid(camelCommentReplyDto.getCommentUUID());
    }


}
