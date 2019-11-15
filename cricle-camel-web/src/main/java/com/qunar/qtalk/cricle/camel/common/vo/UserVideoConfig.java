package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class UserVideoConfig implements Serializable {

    /**
     * 用户允许发送的视频时长，单位毫秒
     */
    private Long videoTimeLen;

    /**
     * 用户允许发送的视频大小，单位byte
     */
    private Long videoFileSize;

    /**
     * 用户上传视频解析清晰度是否高清，默认false
     */
    private Boolean highDefinition = false;
}
