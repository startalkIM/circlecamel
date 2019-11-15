package com.qunar.qtalk.cricle.camel.common.config;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@EnableConfigurationProperties({VideoProperties.class})
public class VideoConfig {

    private Map<String,String> videoTimeLenConfigMap  = Maps.newHashMap();

    private Map<String,String> videoFileSizeConfigMap = Maps.newHashMap();

    private Map<String,String> videoHighDefinitionConfigMap = Maps.newHashMap();

    private VideoProperties videoProperties;

    public VideoConfig(VideoProperties videoProperties) {
        this.videoProperties = videoProperties;

        String timeLen          = videoProperties.getTimeLen();
        String fileSize         = videoProperties.getFileSize();
        String highDefinition   = videoProperties.getHighDefinition();
        if (!Strings.isNullOrEmpty(timeLen)) {
            videoTimeLenConfigMap = Splitter.on(";").trimResults().omitEmptyStrings().withKeyValueSeparator(":").split(timeLen);
        }
        if (!Strings.isNullOrEmpty(fileSize)) {
            videoFileSizeConfigMap = Splitter.on(";").trimResults().omitEmptyStrings().withKeyValueSeparator(":").split(fileSize);
        }
        if (!Strings.isNullOrEmpty(highDefinition)) {
            videoHighDefinitionConfigMap = Splitter.on(";").trimResults().omitEmptyStrings().withKeyValueSeparator(":").split(highDefinition);
        }
        log.info("videoProperties,timeLenConfig:[{}],fileSizeConfig:[{}],highDefinitionConfig:[{}]", JSON.toJSONString(videoTimeLenConfigMap),JSON.toJSONString(videoFileSizeConfigMap),JSON.toJSONString(videoHighDefinitionConfigMap));
    }

    public Integer getVideoTimeLen(String key) {
        return NumberUtils.toInt(Optional.ofNullable(MapUtils.getString(videoTimeLenConfigMap, key)).orElse(videoProperties.getDefaultTimeLen()));
    }

    public Integer getVideoFileSize(String key) {
        return NumberUtils.toInt(Optional.ofNullable(MapUtils.getString(videoFileSizeConfigMap, key)).orElse(videoProperties.getDefaultFileSize()));
    }

    public Boolean getVideoHighDefinition(String key) {
        return BooleanUtils.toBoolean(Optional.ofNullable(MapUtils.getString(videoHighDefinitionConfigMap, key)).orElse(videoProperties.getDefaultHighDefinition()));
    }

}
