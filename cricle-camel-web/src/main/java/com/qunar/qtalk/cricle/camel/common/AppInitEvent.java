package com.qunar.qtalk.cricle.camel.common;

import com.qunar.qtalk.cricle.camel.common.consts.RedisConsts;
import com.qunar.qtalk.cricle.camel.common.redis.RedisAccessor;
import com.qunar.qtalk.cricle.camel.common.util.IPUtils;
import com.qunar.qtalk.cricle.camel.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by haoling.wang on 2019/3/7.
 */
@Slf4j
@Component
public class AppInitEvent implements ApplicationListener<ApplicationReadyEvent> {

    private volatile boolean isInitialed = false;

    @Resource
    private RedisUtil redisUtil;

//    @Resource
//    private CamelCommentService camelCommentService;
//
//    @Resource
//    private CamelAnonymousService camelAnonymousService;

    @Value("${schedule.execute_ip}")
    private String executeIp;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (isInitialed) {
            log.warn("系统已经初始化!!!");
            return;
        }
        String defaultMaxCountTopOrHot = "3";
        String maxCountTopOrHot = defaultMaxCountTopOrHot;
        if (Objects.equals(
                RedisAccessor.execute(key -> redisUtil.setnx(key, defaultMaxCountTopOrHot), RedisConsts.REDIS_MAX_COUNT_TOP_OR_HOT),
                0)) {
            maxCountTopOrHot = RedisAccessor.execute(key -> redisUtil.get(key), RedisConsts.REDIS_MAX_COUNT_TOP_OR_HOT);
        }
        log.info("current MaxCountTopOrHot is {}", maxCountTopOrHot);

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据库的操作放在这儿执行
     */
    private void init() throws IOException {
        log.info("current local ip is:{}", executeIp);
        if (StringUtils.equals(executeIp, IPUtils.getLocalIP())) {
//            //log.info("初始化以前的数据>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//
//            log.info("uodate super parent uuid start...........");
//            camelCommentService.updateSuperParentUUID();
//            log.info("uodate super parent uuid end.............");
//
//            log.info("uodate comment status start..............");
//            camelCommentService.updateStatus();
//            log.info("uodate comment status end................");
//
//            log.info("uodate hot comment start.................");
//            camelCommentService.updatePostHotCommentRedis();
//            log.info("uodate hot comment end...................");
//
//            log.info("初始化以前的数据完成>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            camelAnonymousService.uploadAnonymous();

        }

    }
}
