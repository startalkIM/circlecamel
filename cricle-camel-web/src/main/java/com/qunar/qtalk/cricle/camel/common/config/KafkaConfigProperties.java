package com.qunar.qtalk.cricle.camel.common.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = KafkaConfigProperties.prefix)
public class KafkaConfigProperties {
    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfigProperties.class);
    public static final String prefix = "kafka";
    public static Properties props;
    public String broker;
    public String zookeeper;
    public String groupid;
    public String threadcount;
    public String sendNotifyTopic;
    public String sendMessageTopic;
}
