package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSONObject;
import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.dto.CamelRetainModel;
import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import org.junit.Test;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * CamelStatisicSeriviceTest
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
public class CamelStatisicSeriviceTest extends BaseTest {

    @Resource
    private CamelStatisticDataService camelStatisticDataService;

    @Resource
    private CamelLikeService camelLikeService;

    @Test
    public void testMapper() {

        Date date = Date.valueOf("2019-03-20");
        CamelStatisicDto camelStatisicDto = camelStatisticDataService.getDataByTime(date);
        String brows = camelStatisicDto.getBrowsTimeUser();
        EventModel eventModel;
//        HashMap<String,String> s = (HashMap<String, String>) JSONObject.parseObject(brows,Map.class);
//        //CamelStatisicDto camelStatisicDto = new CamelStatisicDto();
//        Integer activeUserNum = Math.toIntExact(camelStatisticDataService.getActiveUserNum());
//        Integer validActiveUser = Math.toIntExact(camelStatisticDataService.getValidActiveUserNum());
//
//        camelStatisicDto.setActiveNum(activeUserNum);
//        camelStatisicDto.setValidActiveNum(validActiveUser);
//        HashMap<String, String> browsTime = camelStatisticDataService.getBrowsTimeUserNum();
//        camelStatisicDto.setBrowsTimeUser(JacksonUtils.obj2String(browsTime));
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date(System.currentTimeMillis()));
        ca.add(Calendar.DATE, -1);
        Timestamp timestamp = new Timestamp(ca.getTime().getTime());
        camelStatisicDto.setDataTime(timestamp);
        //Date s = new Date(ca.getTime().getTime());
        List<CamelPost> curPost = camelStatisticDataService.getPostsByDate(date);
        List<CamelComment> curCommet = camelStatisticDataService.getCommentsByDate(date);
        List<String> TopPostLike = new ArrayList<>();
        List<String> TopPostCommet = new ArrayList<>();
        Integer anonymousPostNum = 0;
        Integer realNmaePostNum = 0;
        Integer anonymousCommetNum = 0;
        Integer realNameCommetNum = 0;
        Integer maxCommtes = 1;
        Integer maxLike = 1;
        if (curPost != null && curPost.size() > 0) {
            camelStatisicDto.setPostTotalNum(curPost.size());
            for (CamelPost x : curPost) {
                x.setLikeNum(camelLikeService.getCommentLikeCount(x.getUuid()).intValue());
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
                }if (maxCommtes.equals(comments)) {
                    TopPostCommet.add(x.getUuid());
                }
                if (maxLike < like) {
                    TopPostLike.clear();
                    TopPostLike.add(x.getUuid());
                    maxCommtes = like;
                }if (maxLike.equals(comments)) {
                    TopPostLike.add(x.getUuid());
                }
            }
            if(TopPostCommet.size()>0){
                camelStatisicDto.setTopCommentPost(maxCommtes);
                camelStatisicDto.setTopCommentPostuuid(JacksonUtils.obj2String(TopPostCommet));
            }
            if(TopPostLike.size()>0){
                camelStatisicDto.setTopLikePost(maxLike);
                camelStatisicDto.setTopLikePostuuid(JacksonUtils.obj2String(TopPostLike));
            }
            camelStatisicDto.setCommentAnonymousNum(anonymousPostNum);
            camelStatisicDto.setCommentRealnameNum(realNmaePostNum);
        }

        if (curCommet != null && curCommet.size() > 0) {
            camelStatisicDto.setCommentNum(curCommet.size());
            for (CamelComment cp : curCommet) {
                if (cp.getIsAnonymous() == 0) {
                    realNameCommetNum++;
                } else {
                    anonymousCommetNum++;
                }
            }
        }
        camelStatisicDto.setCommentRealnameNum(realNameCommetNum);
        camelStatisicDto.setCommentAnonymousNum(anonymousCommetNum);
        camelStatisticDataService.insertDateCSD(camelStatisicDto);
    }

    @Test
    public void test2(){
        //计算前一天的时间
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date(System.currentTimeMillis()));
        ca.add(Calendar.DATE, -1);
        Timestamp timestamp = new Timestamp(ca.getTime().getTime());
        camelStatisticDataService.camelRetainService(timestamp);

    }
}
