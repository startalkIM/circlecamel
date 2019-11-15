package com.qunar.qtalk.cricle.camel.common.video;

import com.qunar.qtalk.cricle.camel.common.exception.VideoParseException;
import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;

public interface VideoParser {

    /**
     * 解析视频信息
     *
     * @param filePath 视频文件路径
     * @param toolPath 工具指令路径
     * @return
     */
    VideoVo parse(String filePath, String toolPath) throws VideoParseException;

    /**
     * 获取首帧切图
     *
     * @param filePath
     * @param toolPath
     * @param newFilePath
     * @param videoVo
     */
    void getFirstThumb(String filePath, String toolPath, String newFilePath, VideoVo videoVo) throws VideoParseException;

    /**
     * 获取指定位置切图
     *
     * @param filePath
     * @param toolPath
     * @param newFilePath
     * @param videoVo
     */
    void getThumb(String filePath, String toolPath, String newFilePath,
                  VideoVo videoVo, int hour, int minute, int second) throws VideoParseException;

    /**
     * 是否需要转码
     *
     * @param videoVo
     * @return
     */
    boolean isNeedTrans(VideoVo videoVo) throws VideoParseException;

    /**
     * 是否支持快速转码
     *
     * @param videoVo
     * @return
     */
    boolean isSupportQuickTrans(VideoVo videoVo);

    /**
     * 转码
     *
     * @param filePath
     * @param toolPath
     * @param newFilePath
     * @param originVideoVo    原视频对象信息
     * @param remainDefinition 是否保留原视频清晰度
     * @throws VideoParseException
     */
    void trans(String filePath, String toolPath, String newFilePath, VideoVo originVideoVo, boolean remainDefinition) throws VideoParseException;
}
