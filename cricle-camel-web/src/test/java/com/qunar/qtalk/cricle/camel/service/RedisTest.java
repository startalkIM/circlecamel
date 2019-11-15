//package com.qunar.qtalk.cricle.camel.service;
//
//import com.alibaba.fastjson.JSON;
//import com.qunar.qtalk.cricle.camel.BaseTest;
//import com.qunar.qtalk.cricle.camel.common.consts.RedisConsts;
//import com.qunar.qtalk.cricle.camel.common.event.EventModel;
//import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
//import com.qunar.qtalk.cricle.camel.entity.CamelPost;
//import com.qunar.qtalk.cricle.camel.task.UpdateUsersSyncTask;
//import com.qunar.redis.storage.Sedis3;
//import org.junit.Test;
//import redis.clients.jedis.Jedis;
//
//import javax.annotation.Resource;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//
///**
// * Created by haoling.wang on 2019/1/7.
// */
//public class RedisTest extends BaseTest {
//
//    @Resource
//    private Sedis3 sedis3;
//
//    @Resource
//    private CamelAuthService camelAuthService;
//    @Resource
//    private CamelPostService camelPostService;
//    @Resource
//    private UpdateUsersSyncTask updateUsersSyncTask;
//
//    @Test
//    public void test1() {
//
//        String superParentCommentUUID = "1-1f1688ef649e4a83b16fd39aaac4ab61";
//        String postUUID = "0-971CCC0D1936409EA5BB4977AD0186F9";
//        Double hotCommentLikeSum = RedisAccessor.execute(s -> sedis3.zscore(s, superParentCommentUUID),
//                RedisConsts.getRedisCommentLikeRankKeyV2(postUUID));
//
//        System.out.println(hotCommentLikeSum);
//    }
//
//    @Test
//    public void test2() {
//       Set<String> users = sedis3.smembers("cricle_camel:userList");
//       if(users.contains("binz.zhang")){
//           System.out.println(">>>>>>>>>>>>>>>>>>>1");
//       }
//        if(users.contains("juanf.feng")){
//            System.out.println(">>>>>>>>>>>>>>>>>>>2");
//        }
//    }
//
//    @Test
//    public void test3() {
//
//        Set<String> keys = sedis3.hkeys("cricle_camel");
//        System.out.println(keys.size());
//        for (String key:keys) {
//            sedis3.del(key);
//        }
//
//    }
//
//    @Test
//    public void test4() {
//        EventModel eventModel = new EventModel();
//
//        eventModel.setToUser("haoling.wang");
//        String s = JSON.toJSONString(eventModel);
//        System.out.println(s);
//        //sedis3.lpush("myList",s);
//        RedisAccessor.execute(key -> sedis3.lpush(key, s), "event_lista");
//    }
//
//    @Test
//    public void test5() throws InterruptedException {
//        String key = "test:myList";
//
//        new Thread(() -> {
//            while (true) {
//                List<String> brpop = sedis3.brpop(0, key);
//                System.out.println(Thread.currentThread().getName() + " " + Arrays.toString(brpop.toArray()));
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "consumer1").start();
//        new Thread(() -> {
//            while (true) {
//                List<String> brpop = sedis3.brpop(0, key);
//                System.out.println(Thread.currentThread().getName() + " " + Arrays.toString(brpop.toArray()));
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "consumer2").start();
//
//
//        new Thread(() -> {
//            for (int i = 0; i < 100; i++) {
//
//                sedis3.lpush(key, i + "a");
//                System.out.println(Thread.currentThread().getName() + "lpush " + i);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        Thread.sleep(Integer.MAX_VALUE);
//    }
//}
