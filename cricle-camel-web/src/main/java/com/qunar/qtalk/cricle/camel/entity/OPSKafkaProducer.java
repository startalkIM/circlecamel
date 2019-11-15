//package com.qunar.qtalk.cricle.camel.entity;
//
//import com.qunar.qtalk.cricle.camel.common.config.KafkaConfigProperties;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
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
//public final class OPSKafkaProducer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(OPSKafkaConsumer.class);
//    private static volatile KafkaProducer<String, String> opsproducer = null;
//
//    @Resource
//    private KafkaConfigProperties kafkaConfigProperties;
//
//    @PostConstruct
//    private void init() {
//        Properties prop = new Properties();
//        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBroker());
//        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        prop.put(ProducerConfig.BATCH_SIZE_CONFIG, 1024 * 1024  * 5);
//        prop.put(ProducerConfig.LINGER_MS_CONFIG, 10);
//        prop.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864);
//
//        LOGGER.info("the producer prop is {}", prop);
//        opsproducer = new KafkaProducer<>(prop);
//    }
//
//    public static KafkaProducer<String, String> getProducer() {
//        if (opsproducer == null) {
//            synchronized (OPSKafkaProducer.class) {
//                if (opsproducer == null) {
//                    new OPSKafkaProducer();
//                }
//            }
//        }
//        return opsproducer;
//    }
//}
