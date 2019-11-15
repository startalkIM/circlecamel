package com.qunar.qtalk.cricle.camel.task;

import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.schedule.AbstractTask;
import com.qunar.qtalk.cricle.camel.common.vo.PageQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/8.
 */
@Slf4j
public class CamelPostLikeNumSyncTask extends AbstractTask {

    public static final int PAGE_SIZE = 1000;

    /**
     * 默认认为的当前距离上次全量同步之间新增的帖子个数
     */
    public static final int DEFAULT_NEW_POST_COUNT = 100;

    @Resource
    private CamelLikeService camelLikeService;

    @Resource
    private CamelPostService camelPostService;
    
    @Resource
    private CamelPostMapper camelPostMapper;

    public CamelPostLikeNumSyncTask(String taskName, String cron) {
        super(taskName, cron);
    }

    /**
     * 更新redis点赞数到db
     */
    public Pair<Integer, Integer> camelPostLikeNumSyncByPage(List<CamelPost> postList) {
        if (CollectionUtils.isEmpty(postList)) {
            log.warn("{} current execute task list is empty", taskName);
            return null;
        }

        HashMap<String, Integer> postLikeMap = Maps.newHashMap();

        postList.stream().map(CamelPost::getUuid)
                .forEach(postUUID -> {
                    try {
                        Long postLikeCount = camelLikeService.getPostLikeCount(postUUID);
                        if (postLikeCount >= 0) { // 未被删除的帖子
                            postLikeMap.put(postUUID, postLikeCount.intValue());
                        }
                    } catch (Exception e) {
                        log.error("update post [{}] likeNum fail", postUUID, e);
                    }
                });

        return camelPostService.updatePostLikeNum(postLikeMap);
    }

    @Override
    public void doTask() {
        Integer usefulPostMaxId = camelPostService.getUsefulPostMaxId();
        if (usefulPostMaxId > 0) {
            int taskSuccess = 0, taskFail = 0;

            PageQueryVo pageQueryVo = new PageQueryVo(PAGE_SIZE, usefulPostMaxId);
            log.info("taskName:{},totalPage:{}", taskName, pageQueryVo.getTotalPage());
            for (int i = 1; i <= pageQueryVo.getTotalPage(); i++) {
                try {
                    pageQueryVo.setCurPage(i);
                    Pair<Integer, Integer> result = camelPostLikeNumSyncByPage(camelPostService.getUsefulPostList(pageQueryVo));
                    taskSuccess += result.getLeft();
                    taskFail += result.getRight();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("update post likeNum,success:{},fail:{}", taskSuccess, taskFail);
        }
    }


    /**
     * 同步最新的帖子点赞数到db(增量同步)
     */
    public void incrementSyncLikeNum() {
        List<CamelPost> camelPosts = camelPostMapper.selectNewExistPost(DEFAULT_NEW_POST_COUNT);
        Pair<Integer, Integer> syncResult
                = camelPostLikeNumSyncByPage(camelPosts);
        log.info("incrementSyncLikeNum sync result:success:{},fail:{}", syncResult.getLeft(), syncResult.getRight());
    }
}
