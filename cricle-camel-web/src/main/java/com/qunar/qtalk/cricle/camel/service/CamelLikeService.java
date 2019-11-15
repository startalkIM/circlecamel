package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.LikeTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.consts.RedisConsts;
import com.qunar.qtalk.cricle.camel.common.dto.LikeDto;
import com.qunar.qtalk.cricle.camel.common.dto.LikeResultDto;
import com.qunar.qtalk.cricle.camel.common.event.LikeEventProducer;
import com.qunar.qtalk.cricle.camel.common.exception.RedisOpsException;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.*;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelLike;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelLikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum.OP_COMMENT;
import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.LIKE_GET_COMMENT_LIKENUM_ERROR;
import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.LIKE_GET_POST_LIKENUM_ERROR;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Slf4j
@Service
public class CamelLikeService {

    @Resource
    private LikeEventProducer likeEventProducer;

    @Resource
    private CamelPostService camelPostService;

    @Resource
    private CamelCommentService camelCommentService;

    @Resource
    private CamelLikeMapper camelLikeMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Resource
    private CamelCommentMapper camelCommentMapper;

    public String getLikeKey(OpTypeEnum opTypeEnum, String targetId) {
        return String.format(RedisConsts.REDIS_LIKE, opTypeEnum.getName(), targetId);
    }

    /**
     * 生成redis的key，并且更新客户端传过来的likeid
     *
     * @param likeDto
     * @return
     */
    public String getKey(LikeDto likeDto) {
        OpTypeEnum opTypeEnum = likeDto.getOpTypeEnum();
        String objId = null;
        if (opTypeEnum == OpTypeEnum.OP_POST) {
            objId = likeDto.getPostId();
        } else if (opTypeEnum == OP_COMMENT) {
            objId = likeDto.getCommentId();
        }

        if (Strings.isNullOrEmpty(objId)) {
            throw new IllegalArgumentException(
                    String.format("no match opTypeEnum,please check args,likeDto:%s", JSON.toJSONString(likeDto)));
        }
        String generateLikeId = IDUtils.generateLikeId(likeDto.getUserId(), objId);
        log.info("client transport likeUUID:{},generateLikeId:{}", likeDto.getLikeId(), generateLikeId);
        likeDto.setLikeId(generateLikeId);

        return getLikeKey(likeDto.getOpTypeEnum(), objId);
    }

    public Long opRedis(String key, LikeDto likeDto) {
        LikeTypeEnum likeTypeEnum = likeDto.getLikeTypeEnum();
        String userId = likeDto.getUserId();
        if (likeTypeEnum == LikeTypeEnum.LIKE) {
            // 按照点赞的时间排序
            return RedisAccessor.execute(s -> redisUtil.zadd(s, DateUtils.getIntervalFromBaseDay(new Date()), userId), key);
        } else {
            return RedisAccessor.execute(s -> redisUtil.zrem(s, userId), key);
        }
    }

    /**
     * @param likeDto
     * @return
     */
    public JsonResult opLike(LikeDto likeDto) {
        checkLikeInfo(likeDto);

        if (!camelPostService.checkPostUseful(likeDto.getPostId())) {
            log.info("like fail POST illegal{}",JacksonUtils.obj2String(likeDto));
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND.getCode(), BaseCode.OP_RESOURCE_NOTFOUND.getMsg());
        }
        if(!camelCommentService.checkComment(likeDto)){
            log.info("like fail comment illegal:{}",JacksonUtils.obj2String(likeDto));
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND.getCode(), BaseCode.OP_RESOURCE_NOTFOUND.getMsg());

        }
        /*
         * zset: postId:{user1,user2,user3}
         */
        String likeKey = getKey(likeDto);
        if (!checkLikeUseful(likeKey, likeDto)) {
            log.warn("like action invalid,likeDto:{}", JacksonUtils.obj2String(likeDto));
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), BaseCode.BADREQUEST.getMsg());
        }
        opRedis(likeKey, likeDto);
        if (likeDto.getOpTypeEnum() == OP_COMMENT) {
            String postId = likeDto.getPostId();
            String commentId = likeDto.getCommentId();
            Long commentLikeCount = getCommentLikeCount(commentId);
            RedisAccessor.execute(s -> redisUtil.zadd(s, commentLikeCount, commentId), RedisConsts.getRedisCommentLikeRankKey(postId));
            // 维护V2版本评论点赞，以父评论及其子评论点赞数总和
            String superParentUUID = likeDto.getSuperParentUUID();
            if (Strings.isNullOrEmpty(superParentUUID)) {
                CamelComment camelComment = camelCommentMapper.selectByCommentUUID(commentId);
                if (camelComment != null) {
                    superParentUUID = camelComment.getSuperParentUUID();
                }
                if (Strings.isNullOrEmpty(superParentUUID)) {
                    superParentUUID = commentId;
                }
            }
            final String superParentCommentUUID = superParentUUID;
            Double score = RedisAccessor.execute(s -> redisUtil.zscore(s, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postId));
            if (score == null) {
                score = 0D;
            }
            final Double curLikeNum = score;
            if (likeDto.getLikeTypeEnum() == LikeTypeEnum.LIKE) {
                RedisAccessor.execute(s -> redisUtil.zadd(s, curLikeNum + 1, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postId));
            } else {
                if (curLikeNum >= 1) {
                    RedisAccessor.execute(s -> redisUtil.zadd(s, curLikeNum - 1, superParentCommentUUID), RedisConsts.getRedisCommentLikeRankKeyV2(postId));
                }
            }
        }
        // storage db
        threadPoolExecutor.submit(() -> saveLikeInfo(likeDto));
        // event notify
        //threadPoolExecutor.submit(() -> likeEventProducer.notifyEvent(likeDto));
        return JsonResultUtils.success(buildLikeResult(likeDto));
    }

    private void checkLikeInfo(LikeDto likeDto) {
        Assert.assertArgNotNull(likeDto, BaseCode.BADREQUEST.getMsg());
        Assert.assertArgNotNull(likeDto.getOpTypeEnum(), BaseCode.BADREQUEST.getMsg());

        if (likeDto.getOpTypeEnum() == OpTypeEnum.OP_POST) {
            Assert.assertArgNotNull(likeDto.getPostId(), "postId is null");
        }
        if (likeDto.getOpTypeEnum() == OP_COMMENT) {
            Assert.assertArgNotNull(likeDto.getCommentId(), "commentId is null");
        }
    }

    private boolean checkLikeUseful(String key, LikeDto likeDto) {

        if(RedisAccessor.execute(s -> redisUtil.exists(s), key)){
            log.info("like the member{}",JacksonUtils.obj2String(RedisAccessor.execute(s -> redisUtil.zrange(s,0,-1), key)));
        }

        //若点赞验证之前没有点过赞
        if (likeDto.getLikeTypeEnum() == LikeTypeEnum.LIKE) {
            return ((!RedisAccessor.execute(s -> redisUtil.exists(s), key)) ||!RedisAccessor.execute(s -> redisUtil.zrange(s,0,-1), key).contains(likeDto.getUserId()));
        }
        //取消点赞验证之前已经点过赞了
        if (likeDto.getLikeTypeEnum() == LikeTypeEnum.DISLIKE ) {
            return (RedisAccessor.execute(s -> redisUtil.exists(s), key) &&  RedisAccessor.execute(s -> redisUtil.zrange(s,0,-1), key).contains(likeDto.getUserId()));
        }
        return true;
    }

    private void saveLikeInfo(LikeDto likeDto) {
        try {
            CamelLike camelLike = new CamelLike();

            String postId = likeDto.getPostId();
            String userId = likeDto.getUserId();
            String userHost = likeDto.getUserHost();
            String commentId = likeDto.getCommentId();

            CamelLike cl = camelLikeMapper.getUserLikeSign(userId, postId, commentId);
            if (cl != null) {
                camelLikeMapper.updateLikeInfo(userId, postId, commentId, likeDto.getLikeTypeEnum().getType(), DateUtils.getCurTimeStamp());
            } else {
                camelLike.setLikeUuid(likeDto.getLikeId());
                camelLike.setLikeOwner(userId);
                camelLike.setPostUuid(postId);
                camelLike.setCommentUuid(commentId);
                camelLike.setCreatTime(DateUtils.getCurTimeStamp());
                camelLike.setOwnerHost(userHost);
                camelLikeMapper.insertSelective(camelLike);
            }
        } catch (Exception e) {
            log.error("saveLikeInfo occur exception,likeDto:{}", JSON.toJSONString(likeDto), e);
        }
    }

    public LikeResultDto buildLikeResult(LikeDto likeDto) {
        LikeResultDto likeResultDto = LikeResultDto.builder()
                .likeId(likeDto.getLikeId()).opType(likeDto.getOpTypeEnum().getType())
                .userId(likeDto.getUserId()).userHost(likeDto.getUserHost()).build();
        OpTypeEnum opTypeEnum = likeDto.getOpTypeEnum();
        String postId = likeDto.getPostId();
        if (opTypeEnum == OpTypeEnum.OP_POST) {
            likeResultDto.setPostId(postId);
            likeResultDto.setLikeNum(getPostLikeCount(postId));
            likeResultDto.setIsLike(LikeTypeEnum.signOf(isLikePost(postId, likeDto.getUserId())).getType());
        } else if (opTypeEnum == OP_COMMENT) {
            String commentId = likeDto.getCommentId();
            likeResultDto.setPostId(postId);
            likeResultDto.setCommentId(commentId);
            likeResultDto.setLikeNum(getCommentLikeCount(commentId));
            likeResultDto.setIsLike(LikeTypeEnum.signOf(isLikeComment(commentId, likeDto.getUserId())).getType());
        }
        if (likeDto.getOpTypeEnum() == OP_COMMENT) {
            likeResultDto.setAttachCommentList(camelCommentService.getPostAttachHotComment(likeDto.getPostId(), likeDto.getAttachCommentCount()));
        }
        return likeResultDto;
    }

    public Long getPostLikeCount(String postId) {
        try {
            String likeKey = getLikeKey(OpTypeEnum.OP_POST, postId);
            return getLikeCount(likeKey);
        } catch (Exception e) {
            log.error("getPostLikeCount ex,postId:{}", postId, e);
            throw new RedisOpsException(e);
        }
    }

    public Long getCommentLikeCount(String commentId) {
        try {
            String likeKey = getLikeKey(OP_COMMENT, commentId);
            return getLikeCount(likeKey);
        } catch (Exception e) {
            log.error("getPostLikeCount ex,commentId:{}", commentId, e);
            throw new RedisOpsException(e);
        }
    }

    public Long getLikeCount(String key) {
        return RedisAccessor.execute(s -> {
            //if (redisUtil.exists(s)) {
            return redisUtil.zcard(s);
            //}
        }, key);
    }

    public Boolean isLikePost(String postId, String userId) {
        String likeKey = getLikeKey(OpTypeEnum.OP_POST, postId);
        return isLike(likeKey, userId);
    }

    public Boolean isLikeComment(String commentId, String userId) {
        String likeKey = getLikeKey(OP_COMMENT, commentId);
        return isLike(likeKey, userId);
    }

    public Boolean isLike(String key, String userId) {
        return RedisAccessor.execute(s -> {
            Boolean exists = redisUtil.exists(s);
            if (exists) {
                return redisUtil.zrank(s, userId) != null;
            }
            return false;
        }, key);
    }

    public Long removePost(String postId) {
        String likeKey = getLikeKey(OpTypeEnum.OP_POST, postId);
        return RedisAccessor.execute(s -> redisUtil.del(s), likeKey);
    }

    public Long removeComment(String commentId) {
        String likeKey = getLikeKey(OP_COMMENT, commentId);
        return RedisAccessor.execute(s -> redisUtil.del(s), likeKey);
    }
}
