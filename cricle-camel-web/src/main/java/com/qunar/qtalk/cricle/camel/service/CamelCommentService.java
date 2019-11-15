package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.*;
import com.qunar.qtalk.cricle.camel.common.dto.*;
import com.qunar.qtalk.cricle.camel.common.event.CommentEventProducer;
import com.qunar.qtalk.cricle.camel.common.exception.MySQLException;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.*;
import com.qunar.qtalk.cricle.camel.common.vo.PageQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import com.qunar.qtalk.cricle.camel.web.controller.CommentController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.qunar.qtalk.cricle.camel.common.config.KafkaConfigProperties.LOGGER;
import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.*;

/**
 * CamelCommentService
 *
 * @author binz.zhang
 * @date 2019/1/8
 */
@Slf4j
@Service
public class
CamelCommentService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private CamelCommentMapper camelCommentMapper;
    @Resource
    private CamelPostMapper camelPostMapper;
    @Resource
    private DozerUtils dozerUtils;
    @Resource
    private CamelLikeService camelLikeService;
    @Resource
    private CommentEventProducer commentEventProducer;
    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;
    @Resource
    private CamelPostService camelPostService;
    @Resource
    private CamelMessageService camelMessageService;
    @Resource
    private CamelAtService camelAtService;
//
//     @Resource
//     private RedisUtil redisUtil;
    /**
     * 上传主方法评论，1.0、2.0均用到
     */
    public JsonResult uploadComment(CamelComment camelComment) {
        // 增加获取superParentUUID方法
        if (!camelPostService.checkPostUseful(camelComment.getPostUUID())) {
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND.getCode(), BaseCode.OP_RESOURCE_NOTFOUND.getMsg());
        }
        if (!checkParentComment(camelComment)) {
            LOGGER.error("checkParentComment fail,param:{}", JSON.toJSONString(camelComment));
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        try {
            uploadCommentIntoSQLAndsendPush(camelComment);
            camelPostService.addCommentNum(camelComment.getPostUUID(), 1);
        } catch (Exception e) {
            LOGGER.error("this comment already exist:{}", e);
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        return JsonResultUtils.success();
    }

    /**
     * 上传子评论
     *
     * @param camelComment
     * @return
     */
    public JsonResult uploadChildComment(CamelComment camelComment) {
        if (Strings.isNullOrEmpty(camelComment.getParentCommentUUID()) || Strings.isNullOrEmpty(camelComment.getSuperParentUUID())
                || !checkCommentUseful(camelComment.getParentCommentUUID())) {
            LOGGER.info("upload child comment fail due to parent uuid is not legal");
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        CamelComment superParent = camelCommentMapper.selectByCommentUUID(camelComment.getSuperParentUUID());
        //校验主评论是否在该条帖子下面
        if (superParent == null || !superParent.getPostUUID().equals(camelComment.getPostUUID())) {
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND.getCode(), BaseCode.OP_RESOURCE_NOTFOUND.getMsg());
        }
        JsonResult result = uploadComment(camelComment);
        if (!result.isRet()) {
            return result;
        }
        List<CamelComment> cp = new ArrayList<>();
        cp.add(superParent);
        CommentResultModel commentResultModel = socketTheSecondComment(cp, camelComment.getPostUUID());
        commentResultModel.setReturnType(1);
        commentResultModel.setAttachCommentList(getPostAttachHotComment(camelComment.getPostUUID(), camelComment.getAttachCommentCount()));
        return JsonResultUtils.success(commentResultModel);
    }

    /**
     * 上传父评论
     *
     * @param camelComment
     * @return
     */
    public JsonResult uploadParentComment(CamelComment camelComment, List<String> hot) {
        JsonResult result = uploadComment(camelComment);
        CommentResultModel commentResultModel;
        if (!result.isRet()) {
            return result;
        }
        try {
            Integer currentId = selectMaxIdByUUID(camelComment.getPostUUID());
            commentResultModel = getTheCommentsVersion2(currentId + 1, 20, camelComment.getPostUUID(), hot);
        } catch (Exception e) {
            LOGGER.error("V2/uploadComment fail", e);
            return JsonResultUtils.fail(BaseCode.DB_ERROR.getCode(), BaseCode.DB_ERROR.getMsg());
        }
        commentResultModel.setReturnType(0);
        commentResultModel.setAttachCommentList(getPostAttachHotComment(camelComment.getPostUUID(), camelComment.getAttachCommentCount()));
        return JsonResultUtils.success(commentResultModel);
    }


    /**
     * insert comment into the pg  and send the push
     *
     * @param camelComment
     */
    public void uploadCommentIntoSQLAndsendPush(CamelComment camelComment) throws MySQLException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        camelComment.setCreateTime(now);
        String commentUUID = camelComment.getCommentUUID();
        String postUUID = camelComment.getPostUUID();
        try {
            camelCommentMapper.insertSelective(camelComment);
        } catch (RuntimeException e) {
            throw new MySQLException("insert comment fail", e);
        }
        threadPoolExecutor.submit(() -> {
            commentEventProducer.produceEvent(camelComment);
        });
        log.info("put comment into redis");
        //将评论放在帖子的评论列表redis
        threadPoolExecutor.submit(() -> {
            RedisAccessor.execute(s -> redisUtil.zadd(s, 0L, commentUUID), RedisConsts.getRedisCommentLikeRankKey(postUUID));
        });
    }


    /**
     * 拉取指定帖子下最热的评论，指定拉取条数。按点赞数排列
     *
     * @param num
     * @param uuid
     * @return
     */
    public List<CamelCommentDto> selectHotComments(Integer num, String uuid) {
        String commentRankKey = RedisConsts.getRedisCommentLikeRankKey(uuid);
        Boolean exists = RedisAccessor.execute(s -> redisUtil.exists(s), commentRankKey);
        List<CamelComment> comments = Lists.newArrayList();
        if (exists) {
            Set<String> commentUUIDList = RedisAccessor.execute(s -> redisUtil.zrevrange(s, 0, num - 1), commentRankKey);
            if (CollectionUtils.isNotEmpty(commentUUIDList)) {
                //热评阈值处理
                List<String> legalCommentUUIDs = commentUUIDList.stream().filter(x -> RedisAccessor.execute(s -> redisUtil.zscore(s, x), commentRankKey) >= ContextConsts.HOT_COMMENT_THRESHOLD).collect(Collectors.toList());
                if (legalCommentUUIDs != null && legalCommentUUIDs.size() > 0)
                    comments = camelCommentMapper.selectByCommentUUIDList(legalCommentUUIDs);
            }
        } else {
            comments = camelCommentMapper.selectHotComments(num, uuid, ContextConsts.HOT_COMMENT_THRESHOLD);
        }
        List<CamelCommentDto> camelCommentDtos = checkCurUserLikeInfo(comments);
        Collections.sort(camelCommentDtos, new Comparator<CamelCommentDto>() {
            @Override
            public int compare(CamelCommentDto o1, CamelCommentDto o2) {
                return -Integer.compare(o1.getLikeNum(), o2.getLikeNum());
            }
        });
        return camelCommentDtos;
    }


    /**
     * 拉取指定帖子下最热的评论，V2版本，指定拉取条数。按点赞数排列
     *
     * @param num
     * @param uuid postUUID
     * @return
     */
    public JsonResult selectHotCommentsV2(Integer num, String uuid) {
        List<String> legalCommentUUIDs = getHotCommentUUID(uuid, num);
        List<CamelComment> comments = getCommentsByCommentsUUUID(legalCommentUUIDs);
        CommentResultModel commentResultModel = socketTheSecondComment(comments, uuid);
        return JsonResultUtils.success(commentResultModel);
    }


    List<CamelComment> getCommentsByCommentsUUUID(List<String> uuids) {
        if (uuids == null) {
            return Collections.emptyList();
        }
        List<CamelComment> list = new ArrayList<>(uuids.size());
        uuids.stream().forEach(x -> {
            CamelComment camelComment = camelCommentMapper.selectByCommentUUID(x);
            if (camelComment != null) {
                list.add(camelComment);
            }
        });
        return list;
    }


    /**
     * 获取指定帖子下面的热门评论的uuid,需要注意的是获取的都是主评论
     *
     * @param postUUID
     * @return
     */
    List<String> getHotCommentUUID(String postUUID, Integer num) {
        String postCommentRankKey = RedisConsts.getRedisCommentLikeRankKeyV2(postUUID);
        List<String> legalCommentUUIDs = Lists.newArrayList();
        if (RedisAccessor.execute(s -> redisUtil.exists(s), postCommentRankKey)) {
            Set<String> commentUUIDList = RedisAccessor.execute(s -> redisUtil.zrevrange(s, 0, num - 1), postCommentRankKey);
            //热评阈值处理
            legalCommentUUIDs = commentUUIDList
                    .stream()
                    .filter(commentUUID -> RedisAccessor.execute(key -> redisUtil.zscore(key, commentUUID), postCommentRankKey) >= ContextConsts.HOT_COMMENT_THRESHOLD)
                    .collect(Collectors.toList());
        }
        return legalCommentUUIDs;
    }


    /**
     * v1.0 拉取指定帖子下的历史评论，指定条数。按照时间排序
     *
     * @param id
     * @param num
     * @param uuid
     * @return
     */
    public JsonResult selectHistoryComments(Integer id, Integer num, String uuid) {
        List<CamelComment> camelComments;
        try {
            camelComments = camelCommentMapper.selectCommentHistory(id, num, uuid);
        } catch (RuntimeException e) {
            throw new MySQLException("select comment history fail", e);
        }
        if (camelComments == null || camelComments.size() == 0) {
            return generatorEmptyResult(uuid);
        }
        List<CamelComment> showTemp = camelComments.stream().filter(camelComment -> !DeleteEnum.codeOf(camelComment.getIsDelete()).isStatus()).collect(Collectors.toList());
        List<CamelCommentDto> showComments = checkCurUserLikeInfo(showTemp);
        List<CamelDelete> camelDeleteList = camelComments.stream()
                .filter(camelComment -> DeleteEnum.codeOf(camelComment.getIsDelete()).isStatus())
                .map(camelComment -> {
                    CamelDelete camelDelete = new CamelDelete();
                    camelDelete.setId(camelComment.getId());
                    camelDelete.setUuid(camelComment.getCommentUUID());
                    camelDelete.setIsDelete(camelComment.getIsDelete());
                    return camelDelete;
                }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("newComment", showComments);
        result.put("deleteComments", camelDeleteList);
        long numComment = RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(uuid));
        result.put("postCommentNum", numComment);
        long numLike = camelLikeService.getPostLikeCount(uuid);
        result.put("postLikeNum", numLike);
        result.put("isPostLike", LikeTypeEnum.signOf(camelLikeService.isLikePost(uuid, CookieAuthUtils.getCurrentUser())).getType());
        return JsonResultUtils.success(result);
    }

    /**
     * 检查当前用户是否对这条评论点赞
     *
     * @param camelComments
     * @return
     */
    public List<CamelCommentDto> checkCurUserLikeInfo(List<CamelComment> camelComments) {
        if (CollectionUtils.isEmpty(camelComments)) {
            log.warn("current don't have camel comment");
            return Collections.emptyList();
        }
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("UNKNOW_");
        List<CamelCommentDto> camelPostDtos = dozerUtils.mapCollection(camelComments, CamelCommentDto.class);
        for (CamelCommentDto camelCommentDto : camelPostDtos) {
            String uuid = camelCommentDto.getCommentUUID();
            Boolean likePost = camelLikeService.isLikeComment(uuid, curUser);
            camelCommentDto.setIsLike(LikeTypeEnum.signOf(likePost).getType());
            try {
                Long commentLikeCount = camelLikeService.getCommentLikeCount(uuid);
                if (commentLikeCount >= 0) {
                    camelCommentDto.setLikeNum(commentLikeCount.intValue());
                }
            } catch (Exception e) {
                log.error("post {} get likeNum from redis occur exception", uuid, e);
            }
        }
        return camelPostDtos;
    }

    public Integer selectMaxIdByUUID(String uuid) throws MySQLException {
        try {
            return camelCommentMapper.selectMaxIdByUUID(uuid);
        } catch (RuntimeException e) {
            throw new MySQLException("select max id by uuid from camel_comment", e);
        }
    }

    /**
     * 获取帖子下的全部评论
     *
     * @param commentUUID
     * @return
     */
    public CamelComment getCamelCommentByUUID(String commentUUID) {
        Assert.assertArg(!Strings.isNullOrEmpty(commentUUID), "getCamelComment commentUUID is nullOrEmpty");
        return camelCommentMapper.selectByCommentUUID(commentUUID);
    }

    /**
     * 校验评论是否有效
     *
     * @param commentUUID
     * @return true 有效
     */
    public boolean checkCommentUseful(String commentUUID) {
        CamelComment camelCommentByUUID = getCamelCommentByUUID(commentUUID);
        if (camelCommentByUUID != null && !DeleteEnum.codeOf(camelCommentByUUID.getIsDelete()).isStatus()) {
            return true;
        }
        return false;
    }

    public CommentlDeleteResultV1Dto deleteCommentService(String postUUID, String commentUUID) {
        CommentlDeleteResultV1Dto commentlDeleteResultV1Dto = new CommentlDeleteResultV1Dto();
        if (!camelPostService.checkPostUseful(postUUID)) {
            log.warn("delete the comment fail due to the post have already delete");
            return null;
        }
        commentlDeleteResultV1Dto.setCommentUUID(commentUUID);
        commentlDeleteResultV1Dto.setIsDelete(1);
        long numComment = getTheCommentNumByPostUUID(postUUID);
        long numLike = camelLikeService.getPostLikeCount(postUUID);
        commentlDeleteResultV1Dto.setPostCommentNum(numComment);
        commentlDeleteResultV1Dto.setPostLikeNum(numLike);
        if (!isExistComment(commentUUID)) {
            LOGGER.info("delete already delete comment,the commentUUID is {}", commentUUID);
            return commentlDeleteResultV1Dto;
        }
        try {
            deleteComment(postUUID, commentUUID);
            camelPostService.addCommentNum(postUUID, -1);
            if (numComment > 0) {
                commentlDeleteResultV1Dto.setPostCommentNum(numComment - 1);
            } else {
                commentlDeleteResultV1Dto.setPostCommentNum(0);
            }
            //commentlDeleteResultV1Dto.setPostLikeNum(numLike);
        } catch (RuntimeException e) {
            LOGGER.error("delete comment from the camel_comment by uuid fail", e);
            return null;
        }
        return commentlDeleteResultV1Dto;
    }


    /**
     * get the comment num by postUUID
     */
    public long getTheCommentNumByPostUUID(String postUUID) {
        long num = 0;
        if (RedisAccessor.execute(s -> redisUtil.exists(s), RedisConsts.getRedisCommentLikeRankKey(postUUID))) {
            return RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(postUUID));
        }
        return num;
    }

    /**
     * 删除指定id评论
     *
     * @param postUUID
     * @param commentUUID
     * @return
     */
    public Integer deleteComment(String postUUID, String commentUUID) {
        Integer count = camelCommentMapper.deleteCommentByUUID(commentUUID);
        //2.0 新加的逻辑，删除评论的时候，若删除的主评论需查看一下该主评论下是否还有未删除的评论，有则status置为01 没有则置为10
        //若删除的子评论，也需根据主评论下的子评论的状态改变主评论的状态
        CamelComment camelComment = camelCommentMapper.selectByCommentUUID(commentUUID);
        CamelComment superParent;
        String superParentUUID = camelComment.getSuperParentUUID();
        if (Strings.isNullOrEmpty(superParentUUID)) {
            superParentUUID = commentUUID;
            superParent = camelComment;
        } else {
            superParent = camelCommentMapper.selectByCommentUUID(superParentUUID);
        }
        // 检验主评论的isDelete标识，在主评论已经被删除的情况下才会考虑comment_status这个字段的更新
        if (superParent.getIsDelete().equals(1)) {
            List<CamelComment> child = camelCommentMapper.selectExistChild(superParentUUID);
            if (child == null || child.size() == 0) {
                camelCommentMapper.updateCommentStatus(superParentUUID, 2);
            } else {
                camelCommentMapper.updateCommentStatus(superParentUUID, 1);
            }
        }
        /**
         * 3.0 新增逻辑（判断当前删除的评论是否@过人，如果有，则删除消息表中eventtype是评论@的记录）
         */
        String atList = camelComment.getAtList();
        if (!Strings.isNullOrEmpty(atList)) {
            camelMessageService.updateCommentAtMessage2DeleteFlag(commentUUID,null);
        }

        threadPoolExecutor.submit(() -> {
            String superParentCommentUUID = camelComment.getSuperParentUUID();
            if (Strings.isNullOrEmpty(superParentCommentUUID)) {
                RedisAccessor.execute(s -> redisUtil.zrem(s, commentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
            } else {
                if (RedisAccessor.execute(s -> redisUtil.exists(s), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID))) {
                    Long curCommentLikeCount = camelLikeService.getCommentLikeCount(commentUUID);
                    Double hotCommentLikeSum = RedisAccessor.execute(s -> redisUtil.zscore(s, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
                    if (hotCommentLikeSum != null && hotCommentLikeSum >= curCommentLikeCount) {
                        RedisAccessor.execute(s -> redisUtil.zadd(s, hotCommentLikeSum - curCommentLikeCount, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
                    }
                    if (hotCommentLikeSum != null && hotCommentLikeSum <= curCommentLikeCount) {
                        RedisAccessor.execute(s -> redisUtil.zadd(s, 0, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
                    }
                }
            }
            RedisAccessor.execute(s -> redisUtil.zrem(s, commentUUID), RedisConsts.getRedisCommentLikeRankKey(postUUID));
            Long commentLikeCount = camelLikeService.getCommentLikeCount(commentUUID);
            if (commentLikeCount >= 0) {
                updateCommentLikeNumByUUID(commentUUID, commentLikeCount.intValue());
                camelLikeService.removeComment(commentUUID);
            }
        });
        return count;
    }

    /**
     * 判断评论是否被删除，true 表示未被删除，false 表示已被删除
     *
     * @param commentUUID
     * @return
     */
    public boolean isExistComment(String commentUUID) {
        return (camelCommentMapper.selectDeleteFlag(commentUUID).equals(0));
    }

    /**
     * generator empty result
     */
    public JsonResult generatorEmptyResult(String uuid) {
        List<String> newComment = Lists.newArrayList();
        List<String> deleteComments = Lists.newArrayList();
        Map<String, Object> result = new HashMap<>();
        result.put("newComment", newComment);
        result.put("deleteComments", deleteComments);
        long numComment = RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(uuid));
        result.put("postCommentNum", numComment);
        long postNumLike = camelLikeService.getPostLikeCount(uuid);
        result.put("postLikeNum", postNumLike);
        result.put("isPostLike", LikeTypeEnum.signOf(camelLikeService.isLikePost(uuid, CookieAuthUtils.getCurrentUser())).getType());
        return JsonResultUtils.success(result);
    }

    public CommentResultModel generatorEmptyCommentResultModel(String uuid) {
        CommentResultModel commentResultModel = new CommentResultModel();
        List<CamelCommentV2Dto> newComment = Lists.newArrayList();
        List<CamelDelete> deleteComments = Lists.newArrayList();
        commentResultModel.setNewComment(newComment);
        commentResultModel.setDeleteComments(deleteComments);
        long numComment = RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(uuid));
        commentResultModel.setPostCommentNum(numComment);
        long postNumLike = camelLikeService.getPostLikeCount(uuid);
        commentResultModel.setPostLikeNum(postNumLike);
        commentResultModel.setIsPostLike(Integer.valueOf((LikeTypeEnum.signOf(camelLikeService.isLikePost(uuid, CookieAuthUtils.getCurrentUser())).getType())));
        return commentResultModel;
    }

    public Integer getUsefulCommentMaxId() {
        return camelCommentMapper.getUsefulCommentMaxId();
    }

    public List<CamelComment> getUsefulCommentList(PageQueryVo pageQueryVo) {
        Assert.assertArgNotNull(pageQueryVo, "page info is null");

        return camelCommentMapper.getUsefulCommentList(pageQueryVo);
    }

    /**
     * 批量更新评论数
     *
     * @param commentLikeMap
     * @return
     */
    public Pair<Integer, Integer> batchUpdateCommentLikeNum(Map<String, Integer> commentLikeMap) {
        int successNum = 0, failNum = 0;
        if (MapUtils.isNotEmpty(commentLikeMap)) {
            for (Map.Entry<String, Integer> entry : commentLikeMap.entrySet()) {
                String k = entry.getKey();
                Integer v = entry.getValue();
                try {
                    updateCommentLikeNumByUUID(k, v);
                    successNum++;
                } catch (Exception e) {
                    log.error("update comment likeNum fail,post_uuid:{},likeNum:{}", k, v, e);
                    failNum++;
                }
            }
        }
        return Pair.of(successNum, failNum);
    }

    /**
     * 更新评论点赞数
     *
     * @param commentUUID
     * @param likeNum
     * @return
     */
    public Integer updateCommentLikeNumByUUID(String commentUUID, Integer likeNum) {
        Assert.assertArg(!Strings.isNullOrEmpty(commentUUID), "commentUUID is nullOrEmpty");
        Assert.assertArg(likeNum != null, "likeNum is null");

        return camelCommentMapper.updateLikeNumByUUID(commentUUID, likeNum);
    }

    /**
     * 校验父评论参数请求是否合法
     *
     * @param camelComment
     * @return true:合法
     */
    public boolean checkParentComment(CamelComment camelComment) {
        Assert.assertArgNotNull(camelComment, "checkParentComment arg is null");
        if (!Strings.isNullOrEmpty(camelComment.getParentCommentUUID())) {
            return !Strings.isNullOrEmpty(camelComment.getToUser());
        }
        //todo 2.0需要加上验证superParentUUID 的逻辑
        if (!Strings.isNullOrEmpty(camelComment.getToUser())) {
            return !Strings.isNullOrEmpty(camelComment.getParentCommentUUID());
        }
        return true;
    }

    /**
     * 2.0后的需求，二级评论与一级评论分开
     *
     * @return
     */
    public CommentResultModel getTheCommentsVersion2(Integer id, Integer num, String uuid, List<String> hotComments) {
        List<CamelComment> camelComments;
        if (hotComments == null) {
            hotComments = getHotCommentUUID(uuid, CommentController.HOT_COMMENTS);
        }
        try {
            if (hotComments != null && hotComments.size() > 0) {
                camelComments = camelCommentMapper.selectCommentHistoryExcludeHotCommentV2(id, num, uuid, hotComments);
            } else {
                camelComments = camelCommentMapper.selectCommentHistoryV2(id, num, uuid);
            }
        } catch (RuntimeException e) {
            throw new MySQLException("select comment history fail", e);
        }
        CommentResultModel commentResultModel = socketTheSecondComment(camelComments, uuid);
        return commentResultModel;
    }

    public List<CamelComment> getTheComment(Integer id, Integer num, String uuid, Boolean boolen) {
        List<CamelComment> camelComments;
        List<String> hot = getHotCommentUUID(uuid, 3);
        if (boolen && hot != null && hot.size() > 0) {
            camelComments = camelCommentMapper.selectCommentHistoryExcludeHotCommentV2(id, num, uuid, hot);
        } else {
            camelComments = camelCommentMapper.selectCommentHistoryV2(id, num, uuid);
        }
        return camelComments;
    }

    /**
     * 2.0方法 comments为一级评论，该方法是为一级评论获取二级评论
     *
     * @param comments
     * @return
     */
    public CommentResultModel socketTheSecondComment(List<CamelComment> comments, String postUUID) {
        if (comments == null) {
            return null;
        }
        if (comments.size() == 0) {
            return generatorEmptyCommentResultModel(postUUID);
        }
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("UNKNOW_");
        CamelCommentV2Dto camelCommentV2Dto;
        List<CamelCommentV2Dto> showComments = new ArrayList<>(comments.size());
        List<CamelDelete> camelDeleteList = new ArrayList<>(comments.size());

        for (CamelComment camelComment : comments) {
            //将不用显示的主评论直接放到delete 列表
            if (camelComment.getCommentStatus().equals(2)) {
                camelDeleteList.add(new CamelDelete(camelComment.getCommentUUID(), camelComment.getId(), 1));
                continue;
            }
            camelCommentV2Dto = dozerUtils.map(camelComment, CamelCommentV2Dto.class);
            Boolean isLike = camelLikeService.isLikeComment(camelComment.getCommentUUID(), curUser);
            camelCommentV2Dto.setIsLike(LikeTypeEnum.signOf(isLike).getType());
            camelCommentV2Dto.setDeleteChild(Collections.emptyList());
            camelCommentV2Dto.setNewChild(Collections.emptyList());
            try {
                Long commentLikeCount = camelLikeService.getCommentLikeCount(camelComment.getCommentUUID());
                if (commentLikeCount >= 0) {
                    camelCommentV2Dto.setLikeNum(commentLikeCount.intValue());
                }
            } catch (Exception e) {
                log.error("post {} get likeNum from redis occur exception", camelComment.getCommentUUID(), e);
            }
            //查询主评论的子评论
            List<CamelComment> childCommet = camelCommentMapper.selectChildComments(camelComment.getCommentUUID(), camelComment.getPostUUID());
            if (childCommet != null && childCommet.size() > 0) {
                List<CamelComment> existChild = childCommet.stream().filter(x -> x.getIsDelete().equals(0)).collect(Collectors.toList());
                camelCommentV2Dto.setNewChild(checkCurUserLikeInfo(existChild));
                childCommet.removeAll(existChild);
                List<CamelDelete> deletes = childCommet.stream().map(cc -> {
                    CamelDelete camelDelete = new CamelDelete();
                    camelDelete.setId(cc.getId());
                    camelDelete.setUuid(cc.getCommentUUID());
                    camelDelete.setIsDelete(cc.getIsDelete());
                    return camelDelete;
                }).collect(Collectors.toList());
                camelCommentV2Dto.setDeleteChild(deletes);
            }
            showComments.add(camelCommentV2Dto);
        }
        CommentResultModel commentResultModel = new CommentResultModel();
        commentResultModel.setNewComment(showComments);
        commentResultModel.setDeleteComments(camelDeleteList);
        long numComment = RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(postUUID));
        commentResultModel.setPostCommentNum(numComment);
        long numLike = camelLikeService.getPostLikeCount(postUUID);
        commentResultModel.setPostLikeNum(numLike);
        commentResultModel.setIsPostLike(Integer.valueOf(LikeTypeEnum.signOf(camelLikeService.isLikePost(postUUID, CookieAuthUtils.getCurrentUser())).getType()));
        return commentResultModel;
    }

    /**
     * 初始化以前的数据，更新superParentUUID
     *
     * @return
     */

    public boolean updateSuperParentUUID() {
        List<CamelComment> comments = camelCommentMapper.selectAllComments();
        CamelComment camelCommentTemp;
        for (CamelComment comment : comments) {
            camelCommentTemp = comment;
            while (camelCommentTemp != null && !Strings.isNullOrEmpty(camelCommentTemp.getParentCommentUUID())) {
                camelCommentTemp = camelCommentMapper.selectByCommentUUID(camelCommentTemp.getParentCommentUUID());
            }
            if (camelCommentTemp != null) {
                comment.setSuperParentUUID(camelCommentTemp.getCommentUUID());
            }
        }
        List<CamelComment> comments1 = comments.stream().filter(x -> !Strings.isNullOrEmpty(x.getParentCommentUUID())).collect(Collectors.toList());
        camelCommentMapper.updateParentsUUID(comments1);
        return true;
    }

    /**
     * 更新以前的数据的comment_status字段
     *
     * @return
     */
    public boolean updateStatus() {
        List<CamelComment> comments = camelCommentMapper.selectAllComments();
        comments.stream().forEach(x -> {
            if (x.getIsDelete().equals(0)) {
                camelCommentMapper.updateCommentStatus(x.getCommentUUID(), 0);
            }
            if (x.getIsDelete().equals(1) && Strings.isNullOrEmpty(x.getSuperParentUUID())) {
                List<CamelComment> child = camelCommentMapper.selectExistChild(x.getCommentUUID());
                if (child == null || child.size() == 0) {
                    camelCommentMapper.updateCommentStatus(x.getCommentUUID(), 2);
                } else {
                    camelCommentMapper.updateCommentStatus(x.getCommentUUID(), 1);
                }
            }
        });
        return true;
    }

    /**
     * 维护原来老评论的点赞数到v2版本的最热评论排序的redis的key中
     *
     * @param postUUID
     * @return
     */
    public boolean updatePostHotCommentV2(String postUUID) {
        List<CamelComment> commentList = camelCommentMapper.selectCommentListByPostUUID(postUUID);
        if (CollectionUtils.isNotEmpty(commentList)) {
            commentList.stream()
                    .forEach(comment -> {
                        try {
                            String superParentUUID = comment.getSuperParentUUID();
                            String commentUUID = comment.getCommentUUID();
                            if (Strings.isNullOrEmpty(superParentUUID) || StringUtils.equals(superParentUUID, commentUUID)) {
                                List<CamelComment> camelComments = camelCommentMapper.selectExistChild(commentUUID);
                                Long parentCommentLikeNum = camelLikeService.getCommentLikeCount(commentUUID);
                                if (CollectionUtils.isNotEmpty(camelComments)) {
                                    for (CamelComment childComment : camelComments) {
                                        if (StringUtils.equals(commentUUID, childComment.getCommentUUID())) {
                                            continue;
                                        }
                                        Long commentLikeCount = camelLikeService.getCommentLikeCount(childComment.getCommentUUID());
                                        parentCommentLikeNum += commentLikeCount;
                                    }
                                }
                                Long parentCommentChildLikeNumSum = parentCommentLikeNum;
                                RedisAccessor.execute(s -> redisUtil.zadd(s, parentCommentChildLikeNumSum, commentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
                            }
                        } catch (Exception e) {
                            log.error("update postHotComment likeNum v2 occur excepiton,postUUID:{},commmentUUID:{}", postUUID, comment.getCommentUUID());
                        }
                    });
        }
        return true;
    }

    public void getTheSuperParentUUID(CamelComment comment) {
        if (comment == null || Strings.isNullOrEmpty(comment.getParentCommentUUID())) {
            return;
        }
        CamelComment camelCommentTemp = comment;
        while (camelCommentTemp != null && !Strings.isNullOrEmpty(camelCommentTemp.getParentCommentUUID())) {
            camelCommentTemp = camelCommentMapper.selectByCommentUUID(camelCommentTemp.getParentCommentUUID());
            comment.setSuperParentUUID(camelCommentTemp.getSuperParentUUID());
        }
        if (camelCommentTemp != null) {
            comment.setSuperParentUUID(camelCommentTemp.getCommentUUID());
        }
    }

    /*
     * 获取帖子的最热评论，指定条数
     *
     * @param postUUID
     * @param num
     * @return
     */
    public List<CamelCommentDto> getPostAttachHotComment(String postUUID, Integer num) {
        String commentRankKey = RedisConsts.getRedisCommentLikeRankKey(postUUID);
        List<CamelComment> comments = Lists.newArrayList();
        if (RedisAccessor.execute(s -> redisUtil.exists(s), commentRankKey)) {
            Set<String> commentUUIDList = RedisAccessor.execute(s -> redisUtil.zrevrange(s, 0, num - 1), commentRankKey);
            if (CollectionUtils.isNotEmpty(commentUUIDList)) {
                List<String> usefulCommentList = Lists.newArrayList();
                commentUUIDList.stream()
                        .forEach(commentUUID -> {
                            if (camelLikeService.getCommentLikeCount(commentUUID).intValue() > 0) {
                                usefulCommentList.add(commentUUID);
                            }
                        });
                if (CollectionUtils.isNotEmpty(usefulCommentList)) {
                    comments = camelCommentMapper.selectByCommentUUIDList(usefulCommentList);
                }
                int size = usefulCommentList.size();
                if (num > size) {
                    int left = num - size;
                    List<CamelComment> camelComments = camelCommentMapper.selectWithoutTargetComment(left, postUUID, usefulCommentList);
                    comments.addAll(camelComments);
                }
            }
        } else {
            comments = camelCommentMapper.selectHotComments(num, postUUID, 0);
        }
        List<CamelCommentDto> camelCommentDtos = checkCurUserLikeInfo(comments);
        Collections.sort(camelCommentDtos, (o1, o2) -> Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime()));
        Collections.sort(camelCommentDtos, (o1, o2) -> -Integer.compare(o1.getLikeNum(), o2.getLikeNum()));
        return camelCommentDtos;
    }

    public void updatePostHotCommentRedis() {
        List<CamelPost> allUserfulPost = camelPostMapper.getAllUserfulPost();
        if (CollectionUtils.isEmpty(allUserfulPost)) {
            return;
        }
        for (CamelPost camelPost : allUserfulPost) {
            updatePostHotCommentV2(camelPost.getUuid());
        }
    }

    public boolean checkEmptyContent(String content) {
        if (Strings.isNullOrEmpty(content) || Strings.isNullOrEmpty(content.trim())) {
            return false;
        }
        return true;
    }

    public boolean checkComment(LikeDto likeDto) {
        if (likeDto.getOpTypeEnum() == OpTypeEnum.OP_COMMENT) {
            CamelComment camelComment = camelCommentMapper.selectByCommentUUID(likeDto.getCommentId());
            if (camelComment.getIsDelete().equals(1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据PostUUID获取该帖子下面所有可用的评论
     *
     * @param postUUID
     * @return
     */
    public List<CamelComment> getUsefulCommentListByPostUUID(String postUUID) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(postUUID));
        return camelCommentMapper.selectCommentListByPostUUID(postUUID);
    }

    /**
     * 根据PostUUID获取要展示的所有评论
     *
     * @param postUUID
     * @return
     */
    public List<CamelComment> getShowCommentListByPostUUID(String postUUID) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(postUUID));
        return camelCommentMapper.selectShowCommentListByPostUUID(postUUID);
    }

}
