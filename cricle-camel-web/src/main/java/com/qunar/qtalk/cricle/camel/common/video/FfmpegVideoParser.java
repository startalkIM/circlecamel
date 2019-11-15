package com.qunar.qtalk.cricle.camel.common.video;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.exception.VideoParseException;
import com.qunar.qtalk.cricle.camel.common.util.FfmpegUtils;
import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FfmpegVideoParser implements VideoParser {

    public static final String DEFAULT_VIDEO_TYPE = "h264";
    public static final String DEFAULT_VIDEO_FILE_SUFFIX = "mp4";

    @Value("${videoToolPath}")
    public String videoToolPath;

    @Override
    public VideoVo parse(String filePath, String toolPath) throws VideoParseException {
        try {
            String commandPath = Strings.isNullOrEmpty(toolPath) ? videoToolPath : toolPath;
            return FfmpegUtils.getVideoVo(filePath, commandPath);
        } catch (Exception e) {
            log.error("parse video to VideoVo exception,filePath:{},toolPath:{}", filePath, toolPath, e);
            throw new VideoParseException(e);
        }
    }

    @Override
    public boolean isNeedTrans(VideoVo videoVo) throws VideoParseException {
//        if (StringUtils.contains(videoVo.getVideoType(), DEFAULT_VIDEO_TYPE)
//                && StringUtils.equalsIgnoreCase(videoVo.getVideoSuffix(), DEFAULT_VIDEO_FILE_SUFFIX)
//                && videoVo.getVideoSize() < 10 * 1024 * 1024) { // todo
//            return false;
//        }
//        return true;
        return true;
    }

    @Override
    public void getFirstThumb(String filePath, String toolPath, String newFilePath, VideoVo videoVo) throws VideoParseException {
        getThumb(filePath, toolPath, newFilePath, videoVo, 00, 00, 00);
    }

    @Override
    public void getThumb(String filePath, String toolPath, String newFilePath, VideoVo videoVo, int hour, int minute, int second) throws VideoParseException {
        try {
            Integer width = Strings.isNullOrEmpty(videoVo.getWidth()) ? FfmpegUtils.DEFAULT_FIRST_THUMB_WIDTH : Integer.valueOf(videoVo.getWidth());
            Integer height = Strings.isNullOrEmpty(videoVo.getHeight()) ? FfmpegUtils.DEFAULT_FIRST_THUMB_HEIGHT : Integer.valueOf(videoVo.getHeight());
            log.info("getFirstThumb width:{},height:{}", width, height);
            String commandPath = Strings.isNullOrEmpty(toolPath) ? videoToolPath : toolPath;
            FfmpegUtils.getThumb(commandPath, filePath,
                    newFilePath,
                    width, height, hour, minute, second);
        } catch (Exception e) {
            log.error("getThumb occur exception,filePath:{},toolPath:{},newFilePath:{},videoVo:{},hour:{},minute:{},second:{}",
                    filePath, toolPath, newFilePath, JSON.toJSONString(videoVo), hour, minute, second, e);
            throw new VideoParseException(e);
        }
    }

    @Override
    public boolean isSupportQuickTrans(VideoVo videoVo) {
//        if (videoVo != null && StringUtils.contains(videoVo.getVideoType(), DEFAULT_VIDEO_TYPE)) {
//            return true;
//        }
        return false;
    }

    @Override
    public void trans(String filePath, String toolPath, String newFilePath, VideoVo originVideoVo, boolean highDefinition) throws VideoParseException {
        try {
            String commandPath = Strings.isNullOrEmpty(toolPath) ? videoToolPath : toolPath;
            FfmpegUtils.trans(commandPath, filePath, newFilePath, originVideoVo, isSupportQuickTrans(originVideoVo), highDefinition);
        } catch (Exception e) {
            log.error("trans video occur exception,filePath:{},toolPath:{},newFilePath:{}",
                    filePath, toolPath, newFilePath, e);
            throw new VideoParseException(e);
        }
    }
}
