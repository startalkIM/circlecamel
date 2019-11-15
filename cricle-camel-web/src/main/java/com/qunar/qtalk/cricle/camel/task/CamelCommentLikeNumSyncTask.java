package com.qunar.qtalk.cricle.camel.task;

import com.google.common.collect.Maps;
import com.qunar.qtalk.cricle.camel.common.schedule.AbstractTask;
import com.qunar.qtalk.cricle.camel.common.vo.PageQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.service.CamelCommentService;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/8.
 * <p>
 * 评论点赞数定时同步任务
 */
@Slf4j
public class CamelCommentLikeNumSyncTask extends AbstractTask {

    public static final int PAGE_SIZE = 1000;

    @Resource
    private CamelLikeService camelLikeService;

    @Resource
    private CamelCommentService camelCommentService;

    public CamelCommentLikeNumSyncTask(String taskName, String cron) {
        super(taskName, cron);
    }

    /**
     * 更新redis点赞数到db
     */
    public Pair<Integer, Integer> camelCommentLikeNumSyncByPage(List<CamelComment> commentList) {
        if (CollectionUtils.isEmpty(commentList)) {
            log.warn("{} current execute task list is empty", taskName);
            return null;
        }

        HashMap<String, Integer> commentLikeMap = Maps.newHashMap();

        commentList.stream().map(CamelComment::getCommentUUID)
                .forEach(commentUUID -> {
                    try {
                        Long commentLikeCount = camelLikeService.getCommentLikeCount(commentUUID);
                        if (commentLikeCount >= 0) {
                            commentLikeMap.put(commentUUID, commentLikeCount.intValue());
                        }
                    } catch (Exception e) {
                        log.error("update comment [{}] likeNum fail", commentUUID, e);
                    }
                });

        return camelCommentService.batchUpdateCommentLikeNum(commentLikeMap);
    }

    @Override
    public void doTask() {
        Integer usefulCommentMaxId = camelCommentService.getUsefulCommentMaxId();
        if (usefulCommentMaxId > 0) {
            int taskSuccess = 0, taskFail = 0;

            PageQueryVo pageQueryVo = new PageQueryVo(PAGE_SIZE, usefulCommentMaxId);
            log.info("taskName:{},totalPage:{}", taskName, pageQueryVo.getTotalPage());
            for (int i = 1; i <= pageQueryVo.getTotalPage(); i++) {
                try {
                    pageQueryVo.setCurPage(i);
                    Pair<Integer, Integer> result = camelCommentLikeNumSyncByPage(camelCommentService.getUsefulCommentList(pageQueryVo));
                    taskSuccess += result.getLeft();
                    taskFail += result.getRight();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("update comment likeNum,success:{},fail:{}", taskSuccess, taskFail);
        }
    }
}
