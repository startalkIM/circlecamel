package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.VideoInfo;
import lombok.Data;

@Data
public class VideoResultDto extends VideoInfo {

    private String originUrl;

    private String transUrl;

    private String firstThumbUrl;

    private String onlineUrl;

    private boolean ready;

}
