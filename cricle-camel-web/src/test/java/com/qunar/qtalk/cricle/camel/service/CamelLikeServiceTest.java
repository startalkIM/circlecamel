package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.consts.LikeTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import com.qunar.qtalk.cricle.camel.common.dto.LikeDto;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class CamelLikeServiceTest extends BaseTest {

    @Resource
    private CamelLikeService camelLikeService;



    @Test
    public void getLikeKey() {
        String likeKey = camelLikeService.getLikeKey(OpTypeEnum.OP_POST, "abcdef");
        System.out.println(likeKey);
    }

    //@Test
    public String getKey(LikeDto likeDto) {

        String key = camelLikeService.getKey(likeDto);
        System.out.println(key);
        return key;
    }

//    @Test
//    public void opRedis() {
//        String key = "cricle_camel:post:like:0-4a88348dfb94482f8438c90c3e4f750e";
//        System.out.println(sd.size());
//
//
//    }

    @Test
    public void opLike() {
    }

    @Test
    public void getPostLikeCount() {

        System.out.println(camelLikeService.getPostLikeCount("akobray"));

        System.out.println(camelLikeService.isLikePost("akobray", "123"));

        System.out.println(camelLikeService.getCommentLikeCount("123"));
    }

    @Test
    public void geteCommentLikeCount() {
    }
}