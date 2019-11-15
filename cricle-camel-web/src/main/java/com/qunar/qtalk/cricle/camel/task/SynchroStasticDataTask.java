package com.qunar.qtalk.cricle.camel.task;

import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import com.qunar.qtalk.cricle.camel.common.exception.TaskException;
import com.qunar.qtalk.cricle.camel.common.schedule.AbstractTask;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelLikeService;
import com.qunar.qtalk.cricle.camel.service.CamelStatisticDataService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * SynchroStasticDataTask
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
@Slf4j
public class SynchroStasticDataTask extends AbstractTask {
    public SynchroStasticDataTask(String taskName, String cron) {
        super(taskName, cron);
    }


    @Resource
    private CamelStatisticDataService camelStatisticDataService;

    @Resource
    private CamelLikeService camelLikeService;

    @Override
    public void doTask() throws TaskException {
        log.info("statistic task run,curtime{}", System.currentTimeMillis());
        //计算前一天的时间
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date(System.currentTimeMillis()));
        ca.add(Calendar.DATE, -1);
        Timestamp timestamp = new Timestamp(ca.getTime().getTime());
        camelStatisticDataService.camelRetainService(timestamp);
        CamelStatisicDto camelStatisicDto = new CamelStatisicDto();
        Integer activeUserNum = Math.toIntExact(camelStatisticDataService.getActiveUserNum());
        Integer validActiveUser = Math.toIntExact(camelStatisticDataService.getValidActiveUserNum());

        camelStatisicDto.setActiveNum(activeUserNum);
        camelStatisicDto.setValidActiveNum(validActiveUser);
        HashMap<String, String> browsTime = camelStatisticDataService.getBrowsTimeUserNum();
        camelStatisicDto.setBrowsTimeUser(JacksonUtils.obj2String(browsTime));

        camelStatisicDto.setDataTime(timestamp);
        Date s = new Date(ca.getTime().getTime());
        List<CamelPost> curPost = camelStatisticDataService.getPostsByDate(s);
        List<CamelComment> curCommet = camelStatisticDataService.getCommentsByDate(s);
        List<String> TopPostLike = new ArrayList<>();
        List<String> TopPostCommet = new ArrayList<>();
        Integer anonymousPostNum = 0;
        Integer realNmaePostNum = 0;
        Integer anonymousCommetNum = 0;
        Integer realNameCommetNum = 0;
        Integer maxCommtes = 2;
        Integer maxLike = 1;
        Integer LikeTotal = 0;
        if (curPost != null && curPost.size() > 0) {
            camelStatisicDto.setPostTotalNum(curPost.size());
            for (CamelPost x : curPost) {
                x.setLikeNum(camelLikeService.getPostLikeCount(x.getUuid()).intValue());
                LikeTotal = LikeTotal + x.getLikeNum();
                if (x.getIsAnonymous().equals(1)) {
                    anonymousPostNum++;
                } else {
                    realNmaePostNum++;
                }
                Integer comments = x.getCommentsNum();
                Integer like = x.getLikeNum();
                if (maxCommtes < comments) {
                    TopPostCommet.clear();
                    TopPostCommet.add(x.getUuid());
                    maxCommtes = comments;
                }
                if (maxCommtes.equals(comments)) {
                    TopPostCommet.add(x.getUuid());
                }
                if (maxLike < like) {
                    TopPostLike.clear();
                    TopPostLike.add(x.getUuid());
                    maxCommtes = like;
                }
                if (maxLike.equals(comments)) {
                    TopPostLike.add(x.getUuid());
                }
            }
            if (TopPostCommet.size() > 0) {
                camelStatisicDto.setTopCommentPost(maxCommtes);
                camelStatisicDto.setTopCommentPostuuid(JacksonUtils.obj2String(TopPostCommet));
            }
            if (TopPostLike.size() > 0) {
                camelStatisicDto.setTopLikePost(maxLike);
                camelStatisicDto.setTopLikePostuuid(JacksonUtils.obj2String(TopPostLike));
            }
            camelStatisicDto.setPostAnonymousNum(anonymousPostNum);
            camelStatisicDto.setPostRealnameNum(realNmaePostNum);
        }

        if (curCommet != null && curCommet.size() > 0) {
            camelStatisicDto.setCommentNum(curCommet.size());
            for (CamelComment cp : curCommet) {
                long x = camelLikeService.getCommentLikeCount(cp.getCommentUUID());
                LikeTotal = Math.toIntExact(LikeTotal + x);
                if (cp.getIsAnonymous() == 0) {
                    realNameCommetNum++;
                } else {
                    anonymousCommetNum++;
                }
            }
        }
        camelStatisicDto.setCommentRealnameNum(realNameCommetNum);
        camelStatisicDto.setCommentAnonymousNum(anonymousCommetNum);
        camelStatisicDto.setLikeNum(LikeTotal);
        camelStatisticDataService.insertDateCSD(camelStatisicDto);
    }
}
