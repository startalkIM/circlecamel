//package com.qunar.qtalk.cricle.camel.entity;
//
//import com.qunar.qtalk.cricle.camel.common.config.KafkaConfigProperties;
//import kafka.consumer.ConsumerConfig;
//import kafka.javaapi.consumer.ConsumerConnector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.Properties;
//
///**
// * Author : mingxing.shao
// * Date : 16-3-29
// */
//@Component
//public final class OPSKafkaConsumer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(OPSKafkaConsumer.class);
//    private static volatile ConsumerConnector consumer = null;
//    @Resource
//    private KafkaConfigProperties kafkaConfigProperties;
//
//
//
//    @PostConstruct
//    private void init(){
//        Properties prop = new Properties();
//        prop.put("zookeeper.connect",kafkaConfigProperties.getZookeeper() );
//        prop.put("group.id", kafkaConfigProperties.getGroupid());
//        prop.put("auto.commit.interval.ms", "1000");
//        prop.put("auto.commit.enable","true");
//        prop.put("fetch.message.max.bytes", "5242880");
//        ConsumerConfig consumerConfig = new ConsumerConfig(prop);
//        LOGGER.info("the consumer prop is {}", prop);
//        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
//    }
//
//    public static ConsumerConnector getConsumer() {
//        if (consumer == null) {
//            synchronized (OPSKafkaConsumer.class) {
//                if (consumer == null) {
//                    new OPSKafkaConsumer();
//                }
//            }
//        }
//        return consumer;
//    }
//}
