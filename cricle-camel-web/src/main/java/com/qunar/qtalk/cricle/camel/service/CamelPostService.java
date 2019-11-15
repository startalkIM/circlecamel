package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.consts.*;
import com.qunar.qtalk.cricle.camel.common.dto.*;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.common.event.PostEventProducer;
import com.qunar.qtalk.cricle.camel.common.exception.MySQLException;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.limit.FrequencyLimit;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.*;
import com.qunar.qtalk.cricle.camel.common.vo.*;
import com.qunar.qtalk.cricle.camel.entity.*;
import com.qunar.qtalk.cricle.camel.mapper.CamelAuthMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import com.qunar.qtalk.cricle.camel.service.filter.post.PostFilterChain;
import com.qunar.qtalk.cricle.camel.service.helper.AnonymousInfoHide;
import com.qunar.qtalk.cricle.camel.task.CamelPostLikeNumSyncTask;
import com.qunar.qtalk.cricle.camel.web.controller.CricleEntrance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.qunar.qtalk.cricle.camel.common.consts.QmoConsts.*;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Service
@Slf4j
public class CamelPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelPostService.class);

    @Resource
    private RedisUtil redisUtil;


    @Resource
    private DozerUtils dozerUtils;

    @Resource
    private CamelLikeService camelLikeService;

    @Resource
    private CamelPostMapper camelPostMapper;

    @Resource
    private CamelAuthMapper camelAuthMapper;

    @Resource
    private CamelAuthService camelAuthService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;

    @Resource
    private PostEventProducer postEventProducer;

    @Resource
    private CamelCommentService camelCommentService;

    @Resource
    private PostFilterChain postFilterChain;

    @Resource
    private CamelAtService camelAtService;

    @Resource
    private CamelMessageService camelMessageService;

    @Resource
    private CamelPostLikeNumSyncTask postLikeNumSyncTask;

    @Resource
    private AnonymousInfoHide postAnonymousInfoHide;

    @Resource
    private AnonymousInfoHide commnetAnonymousInfoHide;

    private static final String URL_SLOGN = "分享了一条链接!";
    private static final String VIDEO_SLOGN = "分享了一条视频!";


    @Value("${default_topOrHot_ExistsTime}")
    public Integer topOrHotPostDefaultExistsTime;

    private final static String objPattern = "\\[obj type=\"([\\w]+)\" value=\"([\\S]+)\"([\\w|=|\\s|\\.]+)?\\]";
    private final static Pattern compiledPattern = Pattern.compile(objPattern);

    public void saveCamelPost(CamelPost camelPost) {
        String searchContent = parseContent(camelPost.getContent());
        camelPostMapper.insertCamelPost(camelPost, searchContent);
//        threadPoolExecutor.submit(() -> postEventProducer.produceEvent(camelPost));
        postEventProducer.produceEvent(camelPost);
    }

    public List<CamelPostDto> selectNewPost(Integer param) {
        return checkCurUserLikeInfo(camelPostMapper.selectNewExistPost(param));
    }

    public List<CamelDelete> selectDeletePostById(Integer startId, Integer endId) {
        return camelPostMapper.selectDeletePostById(startId, endId);
    }

    public JsonResult postHistory(PostHistoryReqVo postHistoryReqVo) {
        List<CamelPost> camelPostList = camelPostMapper.selectPostHistory(postHistoryReqVo.getCurPostId(), postHistoryReqVo.getPageSize());
        List<CamelPost> showCamelPostList = camelPostList.stream().filter(camelPost -> !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()).collect(Collectors.toList());
        List<CamelDelete> camelDeleteList = camelPostList.stream()
                .filter(camelPost -> DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus())
                .map(camelPost -> {
                    CamelDelete camelDelete = new CamelDelete();
                    camelDelete.setId(camelPost.getId());
                    camelDelete.setUuid(camelPost.getUuid());
                    camelDelete.setIsDelete(camelPost.getIsDelete());
                    return camelDelete;
                }).collect(Collectors.toList());
        List<CamelPostDto> camelPostDtos = checkCurUserLikeInfo(showCamelPostList);
        Map<String, Object> result = new HashMap<>();
        result.put("newPost", camelPostDtos);
        result.put("deletePost", camelDeleteList);
        return JsonResultUtils.success(result);
    }

    public JsonResult postHistoryByUser(PostHistoryByUserVo postHistoryReqVo) {
        List<CamelPost> camelPostList = camelPostMapper.selectPostByUser(postHistoryReqVo.getOwner(), postHistoryReqVo.getOwnerHost()
                , postHistoryReqVo.getCurPostId(), postHistoryReqVo.getPageSize());
        List<CamelPost> showCamelPostList = camelPostList.stream().filter(camelPost -> !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()).collect(Collectors.toList());
        List<CamelDelete> camelDeleteList = camelPostList.stream()
                .filter(camelPost -> DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus())
                .map(camelPost -> {
                    CamelDelete camelDelete = new CamelDelete();
                    camelDelete.setId(camelPost.getId());
                    camelDelete.setUuid(camelPost.getUuid());
                    camelDelete.setIsDelete(camelPost.getIsDelete());
                    return camelDelete;
                }).collect(Collectors.toList());
        List<CamelPostDto> camelPostDtos = checkCurUserLikeInfo(showCamelPostList);

        Map<String, Object> result = new HashMap<>();
        result.put("newPost", camelPostDtos);
        result.put("deletePost", camelDeleteList);
        return JsonResultUtils.success(result);
    }


    public JsonResult markDeleteFlag(String uuid, int deleteFlag) {
        DeleteEnum deleteEnum = checkPostUseful(uuid) ? DeleteEnum.NO_DELETED : DeleteEnum.DELETED;
        if (deleteFlag == deleteEnum.getCode()) {
            return JsonResultUtils.fail(BaseCode.ERR_POST_DELETE_FLAG_REPEAT_WITH_OP);
        }
        int updateId = camelPostMapper.updateDeleteFlag(uuid, deleteFlag);
        afterMarkDeleteFlag(uuid);
        return JsonResultUtils.success(updateId);
    }

    /**
     * 逻辑删除帖子之后，
     * 1.将redis中的帖子点赞数同步到db,并删除帖子点赞key
     * 2.如果该帖子是置顶或者置热，则将该帖子对应的key过期
     * 3.如果该帖子有@别人，那么针对那些@的人的消息表要置为删除状态，避免客户端拉取@我的列表出现已删除帖子（删除帖子，直接将他下面所有评论中的@的消息也置为删除状态）
     * 4.删除帖子，考虑将他下面所有的评论置为删除状态
     *
     * @param uuid
     */
    public void afterMarkDeleteFlag(final String uuid) {
        threadPoolExecutor.execute(() -> {
            try {
                Long postLikeCount = camelLikeService.getPostLikeCount(uuid);
                if (postLikeCount >= 0) {
                    updatePostLikeNum(ImmutableMap.of(uuid, postLikeCount.intValue()));
                    camelLikeService.removePost(uuid);
                }
                deleteTopService(uuid);
                deleteHotService(uuid);
                markAtPostMessageDeleted(uuid);
                markCommentToDeleted(uuid);
                LOGGER.info("afterMarkDeleteFlag execute success,postUUID:{}", uuid);
            } catch (Exception e) {
                LOGGER.error("Post afterMarkDeleteFlag occur exception,postUUID:{}", uuid, e);
            }
        });
    }

    /**
     * 将删除的帖子下面的所有评论置为删除状态
     *
     * @param uuid
     */
    private void markCommentToDeleted(String uuid) {
        try {
            List<CamelComment> usefulCommentList = camelCommentService.getUsefulCommentListByPostUUID(uuid);
            if (CollectionUtils.isNotEmpty(usefulCommentList)) {
                for (CamelComment camelComment : usefulCommentList) {
                    try {
                        camelCommentService.deleteComment(uuid, camelComment.getCommentUUID());
                    } catch (Exception e) {
                        log.error("after delete post markCommentToDeleted occur exception,postUUID:{},commentUUID:{}", uuid, camelComment.getCommentUUID(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("after delete post markCommentToDeleted occur exception,postUUID:{}", uuid, e);
        }
    }

    /**
     * 删除帖子的时候，判断该帖子是否@过人，如果有，则将@的消息在消息表中置为删除状态
     *
     * @param uuid
     */
    private void markAtPostMessageDeleted(String uuid) {
        try {
            CamelPost camelPost = getCamelPost(uuid);
            String atList = camelPost.getAtList();
            if (Strings.isNullOrEmpty(atList)) {
                return;
            }
            // 如果AtList字段不为空，则表示在消息表中应该存在，存在则置为删除状态
            camelMessageService.updatePostAtMessage2DeleteFlag(Lists.newArrayList(EventType.ATREMINDPOST, EventType.ATREMINDCOMMENT), uuid, null);
        } catch (Exception e) {
            log.error("markAtPostMessageDeleted occur exception,postUUID:{}", uuid, e);
        }
    }

    public Integer selectIdByUid(String uuid) {
        return camelPostMapper.selectIdByUUID(uuid);
    }

    public List<CamelPostDto> checkCurUserLikeInfo(List<CamelPost> camelPostList) {
        if (CollectionUtils.isEmpty(camelPostList)) {
            LOGGER.warn("current don't have camel post");
            return Collections.emptyList();
        }
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("UNKNOW_");
        List<CamelPostDto> camelPostDtos = dozerUtils.mapCollection(camelPostList, CamelPostDto.class);
        camelPostDtos.stream().forEach(camelPostDto -> {
            String uuid = camelPostDto.getUuid();
            Boolean likePost = camelLikeService.isLikePost(uuid, curUser);
            camelPostDto.setIsLike(LikeTypeEnum.signOf(likePost).getType());
            // 帖子类型
            camelPostDto.setPostType(getPostType(uuid));
            try {
                Long postLikeCount = camelLikeService.getPostLikeCount(uuid);
                if (postLikeCount >= 0) {
                    camelPostDto.setLikeNum(postLikeCount.intValue());
                }
            } catch (Exception e) {
                LOGGER.error("post {} get likeNum from redis occur exception", uuid, e);
            }
        });
        Collections.sort(camelPostDtos, (o1, o2) -> -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime()));
        return camelPostDtos;
    }

    /**
     *
     *
     * @param camelPostList
     * @param orderEnum
     * @return
     */


    /**
     * 后台管理展示
     *
     * @param camelPostList
     * @param orderByEnum   前端传过来的，排序器
     * @param orderEnum     {@link PostOrderEnum} 正序/倒序
     * @return
     */
    public List<CamelPostDto> fillLikeInfoAndExtInfo(List<CamelPost> camelPostList, PostOrderByEnum orderByEnum, PostOrderEnum orderEnum) {
        List<CamelPostDto> camelPostDtos = checkCurUserLikeInfo(camelPostList);

        Collections.sort(camelPostDtos, PostOrderByEnum.getCompartor(orderByEnum, orderEnum));

        if (CollectionUtils.isNotEmpty(camelPostDtos)) {
            // fill ownerName
            camelPostDtos.stream()
                    .forEach(camelPostDto -> {
                        try {
                            camelPostDto.setOwnerName(transNameFromOwner(camelPostDto.getOwner(), camelPostDto.getOwnerHost()));
                        } catch (Exception e) {
                            LOGGER.error("fill camelPostDto ownerName occur exception,postUUID:{}", camelPostDto.getUuid(), e);
                        }
                    });
        }
        return camelPostDtos;
    }

    public Integer getUsefulPostMaxId() {
        return camelPostMapper.selectUsefulPostMaxId();
    }

    public List<CamelPost> getUsefulPostList(PageQueryVo pageQueryVo) {
        Assert.assertArgNotNull(pageQueryVo, "page info is null");

        return camelPostMapper.getUsefulPostList(pageQueryVo);
    }

    public Pair<Integer, Integer> updatePostLikeNum(Map<String, Integer> postLikeMap) {
        int successNum = 0, failNum = 0;
        if (MapUtils.isNotEmpty(postLikeMap)) {
            for (Map.Entry<String, Integer> entry : postLikeMap.entrySet()) {
                String k = entry.getKey();
                Integer v = entry.getValue();
                try {
                    camelPostMapper.updatePostLikeNum(k, v);
                    successNum++;
                } catch (Exception e) {
                    LOGGER.error("update post likeNum fail,post_uuid:{},likeNum:{}", k, v, e);
                    failNum++;
                }
            }
        }
        return Pair.of(successNum, failNum);
    }

    /**
     * 校验帖子是否有效
     *
     * @param postUUID
     * @return true:有效
     */
    public boolean checkPostUseful(String postUUID) {
        CamelPost camelPost = getCamelPost(postUUID);
        if (camelPost != null && !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()) {
            return true;
        }
        return false;
    }

    public CamelPost getCamelPost(String postUUID) {
        Assert.assertArgNotNull(postUUID, "getCamelPost uuid is null");
        return camelPostMapper.selectByPostUUID(postUUID);
    }

    public CamelPostDto getPostDetailByUUID(String uuid) {
        Assert.assertArg(!Strings.isNullOrEmpty(uuid), "get post detail uuid is null");
        CamelPost camelPost;
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("testUser_");
        camelPost = camelPostMapper.selectByPostUUID(uuid);
        if (camelPost == null) {
            LOGGER.warn("post of uuid {} not exist", uuid);
            return null;
        }
        CamelPostDto camelPostDto = dozerUtils.map(camelPost, CamelPostDto.class);
        Boolean likePost = camelLikeService.isLikePost(uuid, curUser);
        camelPostDto.setIsLike(LikeTypeEnum.signOf(likePost).getType());
        try {
            Long postLikeCount = camelLikeService.getPostLikeCount(uuid);
            if (postLikeCount >= 0) {
                camelPostDto.setLikeNum(postLikeCount.intValue());
            }
        } catch (Exception e) {
            LOGGER.error("post {} get likeNum from redis occur exception", uuid, e);
        }
        return camelPostDto;
    }

    public Integer addCommentNum(String uuid, Integer num) {
        try {
            return camelPostMapper.addCommentNum(uuid, num);
        } catch (RuntimeException e) {
            throw new MySQLException("add comment num into camel_post fail", e);
        }
    }

    public JsonResult<Map> getPostList(PostReqVo postReqVo) {
        Assert.assertArgNotNull(postReqVo, "getPostList reqVo is null");
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("UNKNOW_");
        String curHost = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_DOMAIN)).orElse("UNKNOW_");
        PostQueryDto postQueryDto = transReqVo2Dto(postReqVo);
        List<CamelPost> postList = null;
        /**
         *  falgSelect 拉取标识，true拉取自己的帖子，false 拉取别人的帖子
         */
        Boolean falgSelect = false;

        try {
            postList = camelPostMapper.getPostList(postQueryDto, falgSelect);
        } catch (Exception e) {
            LOGGER.error("get Post From DB occur exception,postReqVo:{}", JSON.toJSONString(postReqVo), e);
        }

        if (CollectionUtils.isNotEmpty(postList)) {
            CamelPost camelPost = postList.stream().min((o1, o2) -> Integer.compare(o1.getId(), o2.getId())).get();
            Integer id = camelPost.getId();
            Timestamp createTime = camelPost.getCreateTime();
            List<CamelPost> sameTimePostList = camelPostMapper.getPostByCreateTime(createTime, id, postQueryDto, falgSelect);
            postList.addAll(sameTimePostList);
        }

        List<CamelPostDto> camelPostDtos = Lists.newArrayList();
        List<CamelDelete> camelDeleteList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(postList)) {
            List<CamelPost> showCamelPostList = postList.stream().filter(camelPost -> !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()).collect(Collectors.toList());
            camelDeleteList = postList.stream()
                    .filter(camelPost -> DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus())
                    .map(camelPost -> {
                        CamelDelete camelDelete = new CamelDelete();
                        camelDelete.setId(camelPost.getId());
                        camelDelete.setUuid(camelPost.getUuid());
                        camelDelete.setIsDelete(camelPost.getIsDelete());
                        return camelDelete;
                    }).collect(Collectors.toList());
            camelPostDtos = checkCurUserLikeInfo(showCamelPostList);
        }

        Collections.sort(camelPostDtos, new Comparator<CamelPostDto>() {
            @Override
            public int compare(CamelPostDto o1, CamelPostDto o2) {
                return -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime());
            }
        });
        Map<String, Object> result = new HashMap<>();
        List<CamelPostDto> postTops = null;
        if ((postReqVo.getPostCreateTime() == null || postReqVo.getPostCreateTime() <= 0)
                && Strings.isNullOrEmpty(postReqVo.getOwner()) && !postReqVo.getGetTop().equals(0)) {
            postTops = getTopPost(); // todo:老版本（置顶），新版本（置顶和置热）顺序
        }
        if (postTops == null) {
            postTops = camelPostDtos;
        } else {
            postTops.addAll(camelPostDtos);
        }
        result.put("newPost", postTops);
        result.put("deletePost", camelDeleteList);
        return JsonResultUtils.success(result);
    }

    /**
     * 获取置顶帖列表
     *
     * @return
     */
    public List<CamelPostDto> getTopPost() {
        return getPostDtoListByUUIDList(getTopPostUUIDList());
    }

    /**
     * 获取置热帖列表
     *
     * @return
     */
    public List<CamelPostDto> getHotPost() {
        return getPostDtoListByUUIDList(getHotPostUUIDList());
    }


    /**
     * 置顶/热 帖
     *
     * @param postUUID
     * @param existTime
     * @return
     */
    public JsonResult setTopOrHotPostService(String postUUID, Integer existTime, ManageOpType opType) {
        Assert.assertArg(!Strings.isNullOrEmpty(postUUID), "setTopOrHot postUUID is nullOrEmpty!!");
        Assert.assertArgNotNull(existTime, "setTopOrHot existTime is null");
        Assert.assertArgNotNull(opType, "setTopOrHot opType is null");
        try {
            if (!checkPostUseful(postUUID)) {
                return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND);
            }
            if (checkPostIsTopOrHot(postUUID)) {
                return JsonResultUtils.fail(BaseCode.ERR_POST_IS_ALREADY_TOP_OR_HOT);
            }
            if (existTime <= 0 || existTime > topOrHotPostDefaultExistsTime) {
                return JsonResultUtils.failWithArgs(BaseCode.ERR_POST_TOP_OR_HOT_EXIST_TIME_INVALID, String.valueOf(topOrHotPostDefaultExistsTime));
            }
            Integer maxTopOrHotCount = getMaxTopOrHotCount();
            if (getPostTopOrHotList().size() >= maxTopOrHotCount) {
                return JsonResultUtils.failWithArgs(BaseCode.ERR_POST_TOP_OR_HOT_IS_OVER, String.valueOf(maxTopOrHotCount));
            }
            switch (opType) {
                case POST_SET_TOP:
                    RedisAccessor.execute(key -> redisUtil.zadd(key, DateUtils.getIntervalFromBaseDay(new Date()), postUUID), RedisConsts.REDIS_TOP_POST);
                    RedisAccessor.execute(key -> redisUtil.setex(key,
                            Math.toIntExact(DateUtils.getSecondsWithHours(existTime)), DateUtils.getCurDateTime()), RedisConsts.getRedisTopPostWithExpire(postUUID));
                    break;
                case POST_SET_HOT:
                    RedisAccessor.execute(key -> redisUtil.zadd(key, DateUtils.getIntervalFromBaseDay(new Date()), postUUID), RedisConsts.REDIS_HOT_POST);
                    RedisAccessor.execute(key -> redisUtil.setex(key,
                            Math.toIntExact(DateUtils.getSecondsWithHours(existTime)), DateUtils.getCurDateTime()), RedisConsts.getRedisHotPostWithExpire(postUUID));
                    break;
                default:
                    return JsonResultUtils.fail(BaseCode.OP_NOT_SUPPORT);
            }
        } catch (Exception e) {
            LOGGER.error("setTopOrHot post fail,postUUID:{},existTime:{} day,opType:{}", postUUID, existTime, opType.name(), e);
            return JsonResultUtils.fail(BaseCode.ERR_POST_SET_TOP);
        }
        return JsonResultUtils.success();
    }


    /**
     * 取消置顶/热 帖
     *
     * @param postUUID
     * @param opType
     * @return
     */
    public JsonResult deleteTopOrHotPostService(String postUUID, ManageOpType opType) {
        Assert.assertArg(!Strings.isNullOrEmpty(postUUID), "deleteTopOrHot postUUID is nullOrEmpty!!");
        Assert.assertArgNotNull(opType, "deleteTopOrHot opType is null");

        try {
            if (!checkPostUseful(postUUID)) {
                return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND);
            }
            switch (opType) {
                case POST_NOT_SET_TOP:
                    return deleteTopService(postUUID);
                case POST_NOT_SET_HOT:
                    return deleteHotService(postUUID);
                default:
                    return JsonResultUtils.fail(BaseCode.OP_NOT_SUPPORT);
            }
        } catch (Exception e) {
            LOGGER.error("deleteTopOrHot post fail,postUUID:{},opType:{}", postUUID, opType.name(), e);
        }
        return JsonResultUtils.fail(BaseCode.ERROR);
    }

    /**
     * 取消置顶贴
     *
     * @param postUUID
     * @return
     */
    public JsonResult deleteTopService(String postUUID) {
        Assert.assertArg(!Strings.isNullOrEmpty(postUUID), "deleteTop postUUID is nullOrEmpty!!");
        try {
            if (!checkPostIsTop(postUUID)) {
                return JsonResultUtils.fail(BaseCode.ERR_POST_IS_NOT_TOP);
            }
            RedisAccessor.execute(key -> redisUtil.del(key), RedisConsts.getRedisTopPostWithExpire(postUUID));
            RedisAccessor.execute(key -> redisUtil.zrem(key, postUUID), RedisConsts.REDIS_TOP_POST);
        } catch (Exception e) {
            LOGGER.error("delete top post error,postUUID:{}", postUUID, e);
            return JsonResultUtils.fail(BaseCode.ERR_POST_NOT_SET_TOP);
        }
        return JsonResultUtils.success();
    }

    /**
     * 取消置热贴
     *
     * @param postUUID
     * @return
     */
    public JsonResult deleteHotService(String postUUID) {
        Assert.assertArg(!Strings.isNullOrEmpty(postUUID), "deleteHot postUUID is nullOrEmpty!!");
        try {
            if (!checkPostIsHot(postUUID)) {
                return JsonResultUtils.fail(BaseCode.ERR_POST_IS_NOT_HOT);
            }
            RedisAccessor.execute(key -> redisUtil.del(key), RedisConsts.getRedisHotPostWithExpire(postUUID));
            RedisAccessor.execute(key -> redisUtil.zrem(key, postUUID), RedisConsts.REDIS_HOT_POST);
        } catch (Exception e) {
            LOGGER.error("delete hot post error,postUUID:{}", postUUID, e);
            return JsonResultUtils.fail(BaseCode.ERR_POST_NOT_SET_HOT);
        }
        return JsonResultUtils.success();
    }

    /**
     * 通过owner获取用户的中文名称
     *
     * @param owner     拼音
     * @param ownerHost 域对象
     * @return
     */
    private String transNameFromOwner(String owner, String ownerHost) {
        Integer hostId = 2;
        if (StringUtils.equals(ownerHost, CricleEntrance.HOST)) {
            hostId = 1;
        }
        CamelUserModel camelUserModel = camelAuthMapper.selectUserModel(owner, hostId);
        return camelUserModel.getUserCName();
    }

    /**
     * 后台管理-帖子详情展示
     *
     * @param postManageVO
     * @return
     */
    public JsonResult postDetailForManagement(PostManageVO postManageVO) {
        CamelPost camelPost = getCamelPost(postManageVO.getPostUUID());
        if (camelPost == null || DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()) {
            return JsonResultUtils.fail(BaseCode.OP_RESOURCE_NOTFOUND);
        }
        PostDetailDto postDetailDto = new PostDetailDto();

        CamelPostDto camelPostDto = dozerUtils.map(camelPost, CamelPostDto.class);

        camelPostDto.setOwnerName(transNameFromOwner(camelPostDto.getOwner(), camelPostDto.getOwnerHost()));
        List<CamelPostManageResultDto> camelPostManageResultDtos = convertPost2ManageResult(Lists.newArrayList(camelPostDto));
        postDetailDto.setPost(camelPostManageResultDtos.get(0));

        List<CamelComment> postCommentList = camelCommentService.getShowCommentListByPostUUID(camelPost.getUuid());
        /**
         * 内存中渲染树形结构，减少db操作
         */
        if (!CollectionUtils.isEmpty(postCommentList)) {
            List<CamelCommentDto> camelCommentDtos = camelCommentService.checkCurUserLikeInfo(postCommentList);
            List<CommentManageDto> commentManageDtoList = camelCommentDtos.stream()
                    .map(camelCommentDto -> {
                        CommentManageDto commentManageDto = dozerUtils.map(camelCommentDto, CommentManageDto.class);
                        if (DeleteEnum.codeOf(camelCommentDto.getIsDelete()).isStatus()) {
                            commentManageDto.setContent("该条评论已删除");
                        }
                        // ownername
                        commentManageDto.setOwnerName(transNameFromOwner(commentManageDto.getFromUser(), commentManageDto.getFromHost()));
                        commnetAnonymousInfoHide.doHideInfo(commentManageDto);
                        return commentManageDto;
                    }).collect(Collectors.toList());
            List<CommentManageDto> parentManageCommnetDtos = commentManageDtoList.stream()
                    .filter(commentManageDto -> Strings.isNullOrEmpty(commentManageDto.getParentCommentUUID())).collect(Collectors.toList());
            parentManageCommnetDtos.stream()
                    .sorted((o1, o2) -> -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime()))
                    .forEach(commentManageDto -> {
                        String commentUUID = commentManageDto.getCommentUUID();
                        List<CamelCommentDto> childCommentList = getCommentDtoFromParentCommentUUID(commentUUID, commentManageDtoList);
                        commentManageDto.setChildComments(childCommentList);
                    });
            postDetailDto.setComments(parentManageCommnetDtos);
        }
        return JsonResultUtils.success(postDetailDto);
    }

    /**
     * 获取未被删除的子评论，先点赞数倒叙排序，再创建时间倒序排序
     *
     * @param parentCommnetUUID
     * @param camelCommentDtos
     * @return
     */
    private List<CamelCommentDto> getCommentDtoFromParentCommentUUID(final String parentCommnetUUID, final List<CommentManageDto> camelCommentDtos) {
        /**
         * 由于评论最多展示两级，所以得依据SuperParentUUID来判断是否是其下评论
         */
        return camelCommentDtos.stream()
                .filter(commentManageDto ->
                        (StringUtils.equals(commentManageDto.getParentCommentUUID(), parentCommnetUUID) || StringUtils.equals(commentManageDto.getSuperParentUUID(), parentCommnetUUID)))
                .filter(commentManageDto -> !DeleteEnum.codeOf(commentManageDto.getIsDelete()).isStatus())
                .sorted((o1, o2) -> -Integer.compare(o1.getLikeNum(), o2.getLikeNum()))
                .sorted((o1, o2) -> -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime()))
                .collect(Collectors.toList());
    }

    private PostQueryDto transReqVo2Dto(PostReqVo postReqVo) {
        PostQueryDto postQueryDto = new PostQueryDto();
        String ownerHost = postReqVo.getOwnerHost();
        String owner = postReqVo.getOwner();
        Long postCreateTime = postReqVo.getPostCreateTime();
        Integer pageSize = postReqVo.getPageSize();
        if (!Strings.isNullOrEmpty(owner)) {
            postQueryDto.setOwner(owner);
        }
        if (!Strings.isNullOrEmpty(ownerHost)) {
            postQueryDto.setOwnerHost(ownerHost);
        }
        if (postCreateTime != null && postCreateTime != 0L) {
            postQueryDto.setPostCreateTime(new Timestamp(postCreateTime));
        }
        if (pageSize != null && pageSize > 0) {
            postQueryDto.setPageSize(pageSize);
        }
        if (postReqVo.getPostType() != null) {
//            if (postReqVo.getPostType() == 1) {
            // 只拉取普通评论
            List<String> postTopOrHotList = getPostTopOrHotList();
            postQueryDto.setExcludePostList(postTopOrHotList);
//            }
        }
        return postQueryDto;
    }

    public ValidateMacTokenResult validateMacToken(HttpServletRequest servletRequest) {
        try {
            String qShareCKey = CookieUtils.getCookieValue(servletRequest, "qshare_ckey");
            Map<String, String> ShareCKeyDecode = CookieAuthUtils.getUserFromCKey(qShareCKey);
            return camelAuthService.checkMacUserToken(ShareCKeyDecode);
        } catch (Exception e) {
            LOGGER.error("validate qshareCkey fali:", e);
            return ValidateMacTokenResult.builder()
                    .validate(false).validateMsg("服务器异常").build();
        }

    }

    /**
     * 判断帖子当前是否置顶
     * return true:是
     **/
    public boolean checkPostIsTop(String postUUID) {
        Boolean isExistTopPostSet = RedisAccessor.execute(key -> {
                    return redisUtil.exists(key) &&
                            redisUtil.zrank(key, postUUID) != null;
                }
                , RedisConsts.REDIS_TOP_POST);
        if (!isExistTopPostSet) {
            return false;
        }
        return RedisAccessor.execute(key -> redisUtil.exists(key)
                , RedisConsts.getRedisTopPostWithExpire(postUUID));
    }

    /**
     * 判断帖子当前是否置热
     **/
    public boolean checkPostIsHot(String postUUID) {
        Boolean isExistHotPostSet = RedisAccessor.execute(key -> {
                    return redisUtil.exists(key) &&
                            redisUtil.zrank(key, postUUID) != null;
                }
                , RedisConsts.REDIS_HOT_POST);
        if (!isExistHotPostSet) {
            return false;
        }
        return RedisAccessor.execute(key -> redisUtil.exists(key)
                , RedisConsts.getRedisHotPostWithExpire(postUUID));
    }

    /**
     * 判断帖子是否置顶或置热
     *
     * @param postUUID
     * @return
     */
    public boolean checkPostIsTopOrHot(String postUUID) {
        return checkPostIsTop(postUUID) || checkPostIsHot(postUUID);
    }

    /**
     * 获取当前置顶/热贴的总和
     *
     * @return
     */
    public List<String> getPostTopOrHotList() {
        List<String> topPostUUIDList = getTopPostUUIDList();
        List<String> hotPostUUIDList = getHotPostUUIDList();
        ArrayList<String> topOrHotPostList = Lists.newArrayList();
        topOrHotPostList.addAll(topPostUUIDList);
        topOrHotPostList.addAll(hotPostUUIDList);
        return topOrHotPostList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取置顶帖帖子列表
     *
     * @return
     */
    public List<String> getTopPostUUIDList() {
        ArrayList<String> postUUIDList = Lists.newArrayList();
        try {
            if (RedisAccessor.execute(key ->
                    redisUtil.exists(key), RedisConsts.REDIS_TOP_POST)) {
                Set<String> postSet = RedisAccessor.execute(key -> redisUtil.zrange(key, 0, -1), RedisConsts.REDIS_TOP_POST);
                if (CollectionUtils.isNotEmpty(postSet)) {
                    List<String> topPostUUIDList = postSet.stream()
                            .filter(postUUID -> RedisAccessor.execute(key -> redisUtil.exists(key), RedisConsts.getRedisTopPostWithExpire(postUUID)))
                            .collect(Collectors.toList());
                    postUUIDList.addAll(topPostUUIDList);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getTopPostUUIDList occur excepiton", e);
        }
        return postUUIDList;
    }

    /**
     * 获取置热帖帖子列表
     *
     * @return
     */
    public List<String> getHotPostUUIDList() {
        ArrayList<String> postUUIDList = Lists.newArrayList();
        try {
            if (RedisAccessor.execute(key ->
                    redisUtil.exists(key), RedisConsts.REDIS_HOT_POST)) {
                Set<String> postSet = RedisAccessor.execute(key -> redisUtil.zrange(key, 0, -1), RedisConsts.REDIS_HOT_POST);
                if (CollectionUtils.isNotEmpty(postSet)) {
                    List<String> topPostUUIDList = postSet.stream()
                            .filter(postUUID -> RedisAccessor.execute(key -> redisUtil.exists(key), RedisConsts.getRedisHotPostWithExpire(postUUID)))
                            .collect(Collectors.toList());
                    postUUIDList.addAll(topPostUUIDList);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getHotPostUUIDList occur excepiton", e);
        }
        return postUUIDList;
    }

    /**
     * 后台帖子页面搜索
     *
     * @param postManageSearchVo
     * @return
     */
    public JsonResult search(PostManageSearchVo postManageSearchVo) {
        try {
            List<String> postTopOrHotList = getPostTopOrHotList();
            if (Objects.equals(postManageSearchVo.getPostType(), PostTypeEnum.TOP_AND_HOT.getType())) {
                List<CamelPost> postDtoListByUUIDList = getPostListByUUIDList(postTopOrHotList);
                return JsonResultUtils.success(new PageResultDto<CamelPostManageResultDto>(postDtoListByUUIDList.size(),
                        convertPost2ManageResult(
                                fillLikeInfoAndExtInfo(postDtoListByUUIDList, PostOrderByEnum.typeOf(postManageSearchVo.getOrderBy()),
                                        PostOrderEnum.typeOf(postManageSearchVo.getOrderFlag()))), 1));
            }

            HashSet<String> userIdFromNameSet = Sets.newHashSet(), userIdFromInputSet = Sets.newHashSet();
            if (!Strings.isNullOrEmpty(postManageSearchVo.getPostOwnerName())) {
                List<CamelUserModel> camelUserModels = camelAuthService.getUserByUserName(postManageSearchVo.getPostOwnerName());
                if (CollectionUtils.isNotEmpty(camelUserModels)) {
                    Set<String> userIdSet = camelUserModels.stream()
                            .map(CamelUserModel::getUserName)
                            .collect(Collectors.toSet());
                    userIdFromNameSet.addAll(userIdSet);
                }
            }
            if (postManageSearchVo.getPostOwerList() != null) {
                userIdFromInputSet.addAll(postManageSearchVo.getPostOwerList());
            }
            Set<String> queryUserIds = CustomCollectionUtils.intersection(userIdFromNameSet, userIdFromInputSet);
            ArrayList<String> postUserIds = Lists.newArrayList(queryUserIds);
            if (CollectionUtils.isNotEmpty(postUserIds) ||
                    StringUtils.isAllEmpty(postManageSearchVo.getPostOwnerName(), postManageSearchVo.getPostOwner())) {
                Integer total = camelPostMapper.getPostListBySearchCount(postUserIds, postManageSearchVo.getPostContent(), postTopOrHotList,
                        postManageSearchVo.getStartDateWithFormat(), postManageSearchVo.getEndDateWithFormat());
                if (total > 0) {
                    /**
                     * 按点赞数排序得先同步最新的一部分帖子的点赞数到db
                     */
                    if (Objects.equals(postManageSearchVo.getPostOrderEnum(), PostOrderByEnum.LIKE_NUM)) {
                        ((CamelPostService) AopContext.currentProxy()).syncLikeNum();
                    }
                    PageQueryVo pageQueryVo = new PageQueryVo(postManageSearchVo.getPageSize(), total);
                    pageQueryVo.setCurPage(postManageSearchVo.getCurPage());

                    List<CamelPost> postList = camelPostMapper.getPostListBySearch(postUserIds, postManageSearchVo.getPostContent(), postTopOrHotList, pageQueryVo,
                            postManageSearchVo.getStartDateWithFormat(), postManageSearchVo.getEndDateWithFormat(), postManageSearchVo.getOrderBy(), postManageSearchVo.getOrderFlag());
                    return JsonResultUtils.success(new PageResultDto<CamelPostManageResultDto>(total, convertPost2ManageResult(
                            fillLikeInfoAndExtInfo(postList, PostOrderByEnum.typeOf(postManageSearchVo.getOrderBy()), PostOrderEnum.typeOf(postManageSearchVo.getOrderFlag()))), pageQueryVo.getTotalPage()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("search post occur exception", e);
        }
        return JsonResultUtils.success(new PageResultDto<CamelPostManageResultDto>(0, Lists.newArrayListWithCapacity(1), 0));
    }

    /**
     * redis控制方法访问频次，5分钟内访问，只做一次
     */
    @FrequencyLimit(time = 1, second = 300)
    public void syncLikeNum() {
        postLikeNumSyncTask.incrementSyncLikeNum();
    }

    /**
     * 将post中的内容转化为后台管理能识别的内容
     *
     * @param camelPostDtoList
     * @return
     */
    private List<CamelPostManageResultDto> convertPost2ManageResult(List<CamelPostDto> camelPostDtoList) {
        if (CollectionUtils.isEmpty(camelPostDtoList)) {
            return Collections.emptyList();
        }
        return camelPostDtoList.stream()
                .map(camelPostDto -> {
                    CamelPostManageResultDto camelPostManageResultDto = dozerUtils.map(camelPostDto, CamelPostManageResultDto.class);
                    postAnonymousInfoHide.doHideInfo(camelPostManageResultDto);
                    try {
                        PostContentDto postCon = new PostContentDto();
                        List<String> imgList = postCon.getImgList();
                        String content = camelPostManageResultDto.getContent();
                        JSONObject contentJsonObj = JSON.parseObject(content);
                        String postContent = contentJsonObj.getString("content");
                        postCon.setContent(postContent);
                        JSONArray jsonArray = contentJsonObj.getJSONArray("imgList");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                String data = (String) jsonObject.get("data");
                                imgList.add(data);
                            }
                        }
                        camelPostManageResultDto.setPostContent(postCon);
                    } catch (Exception e) {
                        LOGGER.error("convertPost2ManageResult occur exception,postUUID:{}", camelPostManageResultDto.getUuid());
                    }
                    return camelPostManageResultDto;
                }).collect(Collectors.toList());
    }

    /**
     * 获取帖子列表，带上帖子相关的评论
     *
     * @param postReqVo
     * @return
     */
    public JsonResult getPostListAttachHotComment(PostReqVo postReqVo) {
        Assert.assertArgNotNull(postReqVo, "getPostListWithHotComment postReqVo is null");
        postFilterChain.setParameter(postReqVo);
        CamelPostResultDto camelPostResultDto = postFilterChain.doFilter();

        List<CamelPostAttachCommentDto> camelPostAttachCommentDtos = camelPostResultDto.getNewPost().stream().map(camelPostDto -> {
            CamelPostAttachCommentDto postAttachCommentDto = dozerUtils.map(camelPostDto, CamelPostAttachCommentDto.class);
            List<CamelCommentDto> postAttachHotComment = camelCommentService.getPostAttachHotComment(camelPostDto.getUuid(), postReqVo.getAttachCommentCount());
            postAttachCommentDto.setAttachCommentList(postAttachHotComment);
            return postAttachCommentDto;
        }).collect(Collectors.toList());
        camelPostResultDto.setNewPost(camelPostAttachCommentDtos);
        return JsonResultUtils.success(camelPostResultDto);
    }

    /**
     * 获取帖子列表，结果包含展示，和删除的帖子
     *
     * @param postReqVo
     * @param falgSelect 拉取标识，true拉取自己的帖子，false 拉取别人的帖子,改字段暂时没用上，可忽略
     */
    public Pair<List<CamelPost>, List<CamelDelete>> getPostList(PostReqVo postReqVo, boolean falgSelect) {
        List<CamelPost> camelPosts = Lists.newArrayList();
        List<CamelDelete> camelDeleteList = Lists.newArrayList();
        try {
            PostQueryDto postQueryDto = transReqVo2Dto(postReqVo);
            List<CamelPost> postList = camelPostMapper.getPostList(postQueryDto, falgSelect);

            if (CollectionUtils.isNotEmpty(postList)) {
                CamelPost camelPost = postList.stream().min(Comparator.comparingInt(CamelPost::getId)).get();
                Integer id = camelPost.getId();
                Timestamp createTime = camelPost.getCreateTime();
                List<CamelPost> sameTimePostList = camelPostMapper.getPostByCreateTime(createTime, id, postQueryDto, falgSelect);
                postList.addAll(sameTimePostList);
            }


            if (CollectionUtils.isNotEmpty(postList)) {
                List<CamelPost> showCamelPostList = postList.stream().filter(camelPost -> !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus()).collect(Collectors.toList());
                camelDeleteList = postList.stream()
                        .filter(camelPost -> DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus())
                        .map(camelPost -> {
                            CamelDelete camelDelete = new CamelDelete();
                            camelDelete.setId(camelPost.getId());
                            camelDelete.setUuid(camelPost.getUuid());
                            camelDelete.setIsDelete(camelPost.getIsDelete());
                            return camelDelete;
                        }).collect(Collectors.toList());
                camelPosts = showCamelPostList;
            }
        } catch (Exception e) {
            LOGGER.error("get Post From DB occur exception,postReqVo:{}", JSON.toJSONString(postReqVo), e);
        }
        return Pair.of(camelPosts, camelDeleteList);
    }

    /**
     * 通过帖子UUID列表获取帖子具体信息
     *
     * @param postUUIDList
     * @return
     */
    public List<CamelPost> getPostListByUUIDList(List<String> postUUIDList) {
        if (CollectionUtils.isNotEmpty(postUUIDList)) {
            List<CamelPost> camelPosts = camelPostMapper.selectByPostUUIDList(postUUIDList);
            camelPosts.stream()
                    .filter(camelPost -> !DeleteEnum.codeOf(camelPost.getIsDelete()).isStatus())
                    .forEach(camelPost -> {
                        try {
                            Long numComment = RedisAccessor.execute(s -> redisUtil.zcard(s), RedisConsts.getRedisCommentLikeRankKey(camelPost.getUuid()));
                            camelPost.setCommentsNum(numComment.intValue());
                        } catch (Exception e) {
                            LOGGER.error("get post commentNum from redis occur exception,postUUID:{}", camelPost.getUuid(), e);
                        }
                    });
            return camelPosts;
        }
        return null;
    }

    /**
     * 通过帖子UUID列表获取帖子具体信息，排序、评论数、点赞数
     *
     * @param postUUIDList
     * @return
     */
    public List<CamelPostDto> getPostDtoListByUUIDList(List<String> postUUIDList) {
        if (CollectionUtils.isNotEmpty(postUUIDList)) {
            List<CamelPost> camelPosts = getPostListByUUIDList(postUUIDList);
            return checkCurUserLikeInfo(camelPosts);
        }
        return null;
    }

    /**
     * 获取帖子的状态
     *
     * @param postUUID
     * @return
     */
    public Integer getPostType(String postUUID) {
        Integer postType = ContextConsts.POST_TYPE_NORMAL;
        try {
            if (checkPostIsTop(postUUID)) {
                postType = ContextConsts.POST_TYPE_TOP;
            } else if (checkPostIsHot(postUUID)) {
                postType = ContextConsts.POST_TYPE_HOT;
            } else {
                postType = ContextConsts.POST_TYPE_NORMAL;
            }
        } catch (Exception e) {
            LOGGER.error("获取帖子状态失败，postUUID:{}", postUUID, e);
        }
        return postType;
    }

    /**
     * 获取当前系统配置的置顶/热的最大数
     *
     * @return
     */
    public Integer getMaxTopOrHotCount() {
        try {
            String s = RedisAccessor.execute(key -> redisUtil.get(key), RedisConsts.REDIS_MAX_COUNT_TOP_OR_HOT);
            return NumberUtils.toInt(s);
        } catch (Exception e) {
            LOGGER.error("manage getMaxTopOrHotCount occur exception", e);
        }
        return 3;
    }

    /**
     * 支持手动设置 置顶/热的最大帖子数
     *
     * @param count
     * @return
     */
    public JsonResult setNewMaxCountTopOrHot(Integer count) {
        Assert.assertArg(count != null && count > 0, "设置的个数应合法");
        Integer beforeCount = getMaxTopOrHotCount();
        RedisAccessor.execute(key -> redisUtil.set(key, String.valueOf(count)), RedisConsts.REDIS_MAX_COUNT_TOP_OR_HOT);
        return JsonResultUtils.success(String.format("设置前:%s,设置后:%d", beforeCount, count));
    }


    public boolean checkPostContent(String content) {
        if (Strings.isNullOrEmpty(content)) {
            return false;
        }
        JSONObject receivedParam = JSON.parseObject(content);
        String content1 = (String) receivedParam.get("content");
        List<String> img = (List) receivedParam.get("imgList");
        if (CollectionUtils.isEmpty(img) && (Strings.isNullOrEmpty(content1) || Strings.isNullOrEmpty(content1.trim()))) {
            return false;
        }
        return true;
    }

    /**
     * 临时加的验证 补偿2.0出现的bug 客户端发版后记得下掉这部分代码
     *
     * @param content
     * @return
     */

    public boolean checkPostImg(String content) {
        try {
            JSONObject receivedParam = JSON.parseObject(content);
            String sd = JacksonUtils.obj2String(receivedParam.get("imgList"));

            if (Strings.isNullOrEmpty(sd)) {
                return true;
            }
            List<PostImagListMode> imgs = JSONObject.parseArray(sd, PostImagListMode.class);

            for (PostImagListMode x : imgs) {
                if (Strings.isNullOrEmpty(x.getData())) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("checkPostImg throw a exception{}", e);
            return true;
        }
        return true;
    }

    /**
     * 3.0补偿客户端分享 缺失title逻辑，客户端补上后 要下掉这部门代码
     *
     * @param param
     */
    public void checkShareTitle(PostUploadVo param) {
        if (Strings.isNullOrEmpty(param.getContent())) {
            return;
        }
        String content1 = param.getContent();
        if (content1 != null) {
            CamelPostContentModel camelPostContentModel = JacksonUtils.string2Obj(content1, CamelPostContentModel.class);
            CamelPostContentModel.LinkContentBean linkContentBean = camelPostContentModel.getLinkContent();
            if (camelPostContentModel.getType() == 2 && linkContentBean != null && (linkContentBean.getTitle() == null || linkContentBean.getTitle().trim().equals(""))) {
                log.info("分享缺失title,增加默认title，postUUID is {}", param.getUuid());
                camelPostContentModel.getLinkContent().setTitle("分享链接");
                param.setContent(JacksonUtils.obj2String(camelPostContentModel));
            }
        }
    }


    /**
     * 解析发帖的 content
     *
     * @param content
     * @return
     */
    public String parseContent(String content) {
        try {
            Map<String, Object> AD = JacksonUtils.string2Map(content);
            String contentBare = (String) AD.get("content");
            String exContent = (String) AD.get("exContent");

            if (Strings.isNullOrEmpty(exContent)) {
                if ((contentBare.equalsIgnoreCase(URL_SLOGN) || contentBare.equalsIgnoreCase(VIDEO_SLOGN))) {
                    return "";
                }
                return contentBare;
            }
            List<Map<String, String>> parseContent = getObjList(exContent);
            StringBuffer stringBuffer = new StringBuffer();
            parseContent.stream().forEach(x -> {
                stringBuffer.append(x.get("value"));
            });
            return stringBuffer.toString();
        } catch (Exception e) {
            LOGGER.error("parse the content error ", e);
        }
        return "";
    }


    /**
     * 解析发帖的 [obj type="url" value="http://www.baidu.com"] etc.
     *
     * @param srcObj
     * @return
     */
    public List<Map<String, String>> getObjList(String srcObj) {
        List<Map<String, String>> result = new ArrayList();
        if (srcObj == null) {
            srcObj = "";
        }
        if (srcObj.length() < 21) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
            return result;
        }

        Matcher m = compiledPattern.matcher(srcObj);
        int start = 0;
        int end = 0;
        while (m.find()) {
            String type = m.group(1);
            String value = m.group(2);
            String ext = null;
            if (m.groupCount() >= 3) {
                ext = m.group(3);
            }
            end = m.start();
            if (end > start) {
                Map<String, String> textMap = new HashMap<String, String>();
                textMap.put("type", "text");
                textMap.put("value", srcObj.substring(start, end));
                result.add(textMap);
            }
            start = m.end();

            Map<String, String> objMap = new HashMap<String, String>();
            objMap.put("type", type);
            objMap.put("value", value);
            if (ext != null) {
                objMap.put("extra", ext);
            }
            result.add(objMap);
        }
        if (start == end) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj);
            result.add(textMap);
        } else if (start > end && start < srcObj.length() - 1) {
            Map<String, String> textMap = new HashMap<String, String>();
            textMap.put("type", "text");
            textMap.put("value", srcObj.substring(start));
            result.add(textMap);
        }
        return result;
    }


    public Integer fixSearchContent(Integer beginIdD, Integer endID) {
        List<CamelPost> posts = camelPostMapper.selectPostByIdScorp(beginIdD, endID);
        AtomicReference<Integer> i = new AtomicReference<>(0);
        if (posts != null) {
            posts.stream().forEach(x -> {
                String searchContent = parseContent(x.getContent());
                camelPostMapper.updateSearchContent(x.getUuid(), searchContent);
                i.getAndSet(i.get() + 1);
            });
        }
        return i.get();
    }


    public Integer getPostLikeNum(String postUUID) {
        return camelPostMapper.getPostLikeNum(postUUID);
    }

    public Integer getPostCommentNum(String postUUID) {
        return camelPostMapper.getPostCommentsNum(postUUID);
    }
}
