package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 视频解析出来的参数信息
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VideoVo implements Serializable {

    private String videoName;

    private String videoMd5;

    private String videoFirstThumb;

    private String videoSuffix;

    private String videoType;

    private String width;

    private String height;

    private Long duration;

    private String bitRate;

    private Long videoSize;

    public VideoVo copy(){
        return VideoVo.builder()
                .videoName(videoName).videoMd5(videoMd5)
                .videoFirstThumb(videoFirstThumb).videoSuffix(videoSuffix)
                .videoType(videoType).width(width).height(height)
                .duration(duration).bitRate(bitRate).videoSize(videoSize)
                .build();
    }

}
