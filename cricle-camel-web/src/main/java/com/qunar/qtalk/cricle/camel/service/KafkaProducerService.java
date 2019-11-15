//package com.qunar.qtalk.cricle.camel.service;
//
//import com.qunar.qtalk.cricle.camel.common.config.KafkaConfigProperties;
//import com.qunar.qtalk.cricle.camel.entity.OPSKafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.PartitionInfo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * ProducerService
// *
// * @author binz.zhang
// * @date 2018/12/24
// */
//@Component
//public class KafkaProducerService {
//    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
//    @Autowired
//    private KafkaConfigProperties kafkaConfigProperties;
//    private String producerTopic = "custom_vs_cricle_camel_sendNotify";
//    // 同步发送消息
//    public void synSendMessage(String key, String msg) {
//        try {
//            logger.info("send kafka message key: {}, msg:{}", key, msg);
//            OPSKafkaProducer.getProducer().send(new ProducerRecord<String, String>(kafkaConfigProperties.sendNotifyTopic, getRandomPartitionByTopic(producerTopic), key, msg));
//        } catch (Exception e) {
//            logger.info("send kafka message fail", e);
//        }
//    }
//    private int getRandomPartitionByTopic(String topic) {
//        List<PartitionInfo> list = OPSKafkaProducer.getProducer().partitionsFor(topic);
//        int partition = list.size() - 1;
//        return (int) (Math.random() * partition);
//    }
//}
