package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.common.event.CommentEventProducer;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//v2版本的service
@Slf4j
@Service
public class CamelCommentV2Service {

    @Resource
    private CamelCommentMapper camelCommentMapper;
    @Resource
    private DozerUtils dozerUtils;
    @Resource
    private CamelLikeService camelLikeService;
    @Resource
    private CommentEventProducer commentEventProducer;
    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;


}
