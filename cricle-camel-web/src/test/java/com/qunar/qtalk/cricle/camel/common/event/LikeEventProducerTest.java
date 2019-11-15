package com.qunar.qtalk.cricle.camel.common.event;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.dto.LikeDto;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by haoling.wang on 2019/1/16.
 */
public class LikeEventProducerTest extends BaseTest {

    @Resource
    private LikeEventProducer likeEventProducer;

    @Test
    public void test1(){

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId("123");
        likeDto.setUserHost("local");
        likeEventProducer.notifyEvent(likeDto);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}