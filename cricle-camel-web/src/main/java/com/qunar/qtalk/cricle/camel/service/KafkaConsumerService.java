//package com.qunar.qtalk.cricle.camel.service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.qunar.qtalk.cricle.camel.common.config.KafkaConfigProperties;
//import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
//import com.qunar.qtalk.cricle.camel.common.event.EventModel;
//import com.qunar.qtalk.cricle.camel.common.event.MessageHandler;
//import com.qunar.qtalk.cricle.camel.common.event.handler.*;
//import com.qunar.qtalk.cricle.camel.common.util.ExecutorUtils;
////import com.qunar.qtalk.cricle.camel.entity.OPSKafkaConsumer;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.message.MessageAndMetadata;
//import org.apache.commons.codec.binary.StringUtils;
//import org.apache.commons.collections4.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Author : malin
// * Date : 18-12-14
// */
//@Service
//public class KafkaConsumerService {
//    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
//    private static volatile List<ConsumerParam> cps = new ArrayList<>();
//
//    @Autowired
//    private KafkaConfigProperties kafkaConfigProperties;
//
//    @Resource
//    private PostHandler postHandler;
//
//    @Resource
//    private LikeHandler likeHandler;
//
//    @Resource
//    private CommentHandler commentHandler;
//    @Resource
//    private CommentAtListHandle commentAtListHandle;
//
//
//    @Resource
//    private PostAtListHandle postAtListHandle;
//    @PostConstruct
//    public void ConsumerService() {
//        sendNotifyQueue();
//        receive(cps);
//    }
//
//
//    private void sendNotifyQueue() {
//        ConsumerParam cp = new ConsumerParam(kafkaConfigProperties.sendNotifyTopic, 1, (key, msg) -> {
//            if (key.equals(ContextConsts.KAFKA_SENDNOTIFY_KEY)) {
//                LOGGER.info("consumer thread consume the msg:{}", msg);
//                EventModel eventModel = JSONObject.parseObject(msg, EventModel.class);
//                switch (eventModel.getEventType()) {
//                    case POST:
//                        postHandler.doHandle(eventModel);
//                        break;
//                    case COMMENT:
//                        commentHandler.doHandle(eventModel);
//                        break;
//                    case LIKE:
//                        break;
//                    case ATREMINDPOST:
//                        postAtListHandle.doHandle(eventModel);
//                        break;
//                    case ATREMINDCOMMENT:
//                        commentAtListHandle.doHandle(eventModel);
//                        break;
//                }
//            }
//            if (key.equals(ContextConsts.KAFKA_SENDMESSAGE_KEY)) {
//                commentAtListHandle.handleAtMessage(msg);
//            }
//        }, ExecutorUtils.newLimitedCachedThreadPool());
//        cps.add(cp);
//    }
//
//    private void receive(List<ConsumerParam> consumerParamList) {
//        if (CollectionUtils.isEmpty(consumerParamList)) {
//            return;
//        }
//        //设置处理消息线程数，线程数应小于等于partition数量，若线程数大于partition数量，则多余的线程则闲置，不会进行工作
//        //key:topic名称 value:线程数
//        Map<String, Integer> topicCountMap = new HashMap<>();
//
//        for (ConsumerParam cp : consumerParamList) {
//            topicCountMap.put(cp.topic, cp.consumeThreadCount);
//        }
//        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = OPSKafkaConsumer.getConsumer().createMessageStreams(topicCountMap);
//
//        for (final ConsumerParam cp : consumerParamList) {
//            //获取对应topic的消息队列
//            List<KafkaStream<byte[], byte[]>> streamList = consumerMap.get(cp.topic);
//            //创建线程池用于消费队列
//            ExecutorService executorService = Executors.newSingleThreadExecutor();
//            LOGGER.info("ThreadId {}, topic ({}), stream list count: {}", Thread.currentThread().getId(), cp.topic, streamList.size());
//            for (final KafkaStream<byte[], byte[]> stream : streamList) {
//                executorService.execute(() -> {
//                    LOGGER.info("executorService 线程内部开始, id: {}", Thread.currentThread().getId());
//                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
//                    while (it.hasNext()) {
//                        MessageAndMetadata<byte[], byte[]> mam = it.next();
//                        byte[] keyBytes = mam.key();
//                        final byte[] msgBytes = mam.message();
//                        //
//                        String key = StringUtils.newStringUtf8(keyBytes);
//                        String msg = StringUtils.newStringUtf8(msgBytes);
//                        LOGGER.info("MQ消息key:[{}],msg:[{}]", key, msg);
//                        //必须成功消费
//                        successConsumeMsg(cp, key, msg);
////                        cp.executor.execute(() -> cp.messageHandler.handle(msgBytes));
//                    }
//                    LOGGER.info("executorService 线程内部结束, id: {}", Thread.currentThread().getId());
//                });
//            }
//        }
//    }
//
//    /**
//     * 必须成功消费
//     * 解决线程池满导致的执行退出问题
//     */
//    private void successConsumeMsg(ConsumerParam cp, String key, String msg) {
//        boolean isSuccess = false;
//        int count = 0;
//        do {
//            try {
//                cp.executor.execute(() -> cp.messageHandler.handle(key, msg));
//                isSuccess = true;
//            } catch (Exception e) {
//                LOGGER.warn("MQ消息topic:[{}], key:[{}],msg:[{}]执行异常，失败重试次数{} ", cp.topic, key, msg, ++count, e);
//                try {
//                    Thread.sleep(5000l);
//                } catch (InterruptedException e1) {
//                    //理论上不应该执行到这
//                    LOGGER.error("sleep 竟然异常", e1);
//                }
//            }
//        } while (!isSuccess);
//    }
//
//    private static class ConsumerParam {
//        private String topic;
//        private int consumeThreadCount;
//        private MessageHandler messageHandler;
//        private Executor executor;
//
//        public ConsumerParam(String topic, int consumeThreadCount, MessageHandler messageHandler, Executor executor) {
//            this.topic = topic;
//            this.consumeThreadCount = consumeThreadCount;
//            this.messageHandler = messageHandler;
//            this.executor = executor;
//        }
//    }
//
//}
//
