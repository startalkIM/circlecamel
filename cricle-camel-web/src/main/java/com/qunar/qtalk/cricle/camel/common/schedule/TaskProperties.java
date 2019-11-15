package com.qunar.qtalk.cricle.camel.common.schedule;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/9.
 */
@Data
@ConfigurationProperties(prefix = TaskProperties.PREFIX)
public class TaskProperties {

    public static final String PREFIX = "schedule";

    private String executeIp;

    private String tasks;

    public Map<String, String> getTaskMap() {
        if (Strings.isNullOrEmpty(tasks)) {
            return null;
        }
        return Splitter.on(",").trimResults().withKeyValueSeparator(":").split(tasks);
    }
}
