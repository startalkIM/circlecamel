package com.qunar.qtalk.cricle.camel.entity;

import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoInfo implements Serializable {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 资源原始名称
     */
    private String originFilename;

    /**
     * 转码后的资源名称
     */
    private String transFilename;

    /**
     * 资源原始md5
     */
    private String originFileMd5;

    /**
     * 转码后的资源md5
     */
    private String transFileMd5;

    /**
     * 转码后的资源截图
     */
    private String firstThumb;

    /**
     * 原始资源信息，json格式
     */
    private VideoVo originFileInfo;

    /**
     * 转码后的资源信息，json格式
     */
    private VideoVo transFileInfo;

    /**
     * 状态
     */
    private Short status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}