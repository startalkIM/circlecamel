package com.qunar.qtalk.cricle.camel.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = VideoProperties.VIDEO_CONFIG_PREFIX)
public class VideoProperties {

    public static final String VIDEO_CONFIG_PREFIX = "video.config";

    private String defaultTimeLen;

    private String defaultFileSize;

    private String defaultHighDefinition;

    private String timeLen;

    private String fileSize;

    private String highDefinition;

}
