package com.qunar.qtalk.cricle.camel.web.controller;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.dto.VideoResultDto;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.store.swift.SwiftConfig;
import com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils;
import com.qunar.qtalk.cricle.camel.common.util.FileUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.common.vo.UserVideoConfig;
import com.qunar.qtalk.cricle.camel.entity.VideoInfo;
import com.qunar.qtalk.cricle.camel.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("newapi/cricle_camel/video")
public class VideoController {

    public static final Long MAX_VIDEO_LENGTH = 30 * 1024 * 1024L;

    @Resource
    private VideoService videoService;

    @Resource
    private SwiftConfig swiftConfig;

    @Value("${video_online_play_base_url}")
    private String onlinePlayBaseUrl;

    /**
     * //生成本地临时文件
     *         // 取文件md5，判断文件是否存在，
     *         // 存在，取对应video info返回
     *         // 不存在，生成新文件名称，并上传对象存储
     *         // 获取文件码率信息，存入db,并判断是否需要转码
     *         // 不需要，直接返回
     *         // 需要，调用转码服务，获取转码后文件信息，上传对象存储
     * @param file
     * @return
     */
    @PostMapping("upload")
    public Object uploadFile(@NotNull(message = "文件不能为空") @RequestParam("file") MultipartFile file,
                             @RequestParam(value = "highDefinition", required = false, defaultValue = "false") String highDefinition) {
        String originalFilename = file.getOriginalFilename();
        log.info("upload file originalFilename:{},highDefinition:{}", originalFilename, highDefinition);

        String fileName = FileUtils.generateLocalSysFileName(originalFilename);
        File newFile = new File(swiftConfig.getTempDirectory(), fileName);

        try {
            file.transferTo(newFile);

            long length = newFile.length();
//            if (length > MAX_VIDEO_LENGTH) {
//                return JsonResultUtils.fail(BaseCode.ERR_FILE_IS_OVER_SIZE);
//            }

            JsonResult<VideoResultDto> videoInfoJsonResult = videoService.doVideoTrans(newFile, BooleanUtils.toBooleanObject(highDefinition));
            if (!videoInfoJsonResult.isRet()) {
                return videoInfoJsonResult;
            }
            VideoResultDto videoResultDto = videoInfoJsonResult.getData();
            videoResultDto.setReady(true);
            log.info("videoResultDto:{}", JSON.toJSONString(videoResultDto));
            return JsonResultUtils.success(videoResultDto);
        } catch (IOException e) {
            log.error("upload file exception,originalFilename:{},fileName:{}", originalFilename, fileName, e);
        } finally {
            if (newFile.exists() && newFile.isFile()) {
                newFile.delete();
                log.info("delete file {}", newFile.getAbsolutePath());
            }
        }
        return JsonResultUtils.fail(500, "处理失败");
    }


    /**
     * 文件md5校验
     *
     * @param map
     * @return
     */
    @PostMapping("check")
    public Object check(@RequestBody Map<String, String> map) {
        log.info("check video args:{}", JSON.toJSONString(map));
        String videoMd5 = map.get("videoMd5");
        VideoInfo videoInfo = videoService.getVideoInfo(videoMd5);
        if (null == videoInfo) {
            VideoResultDto videoCheckDto = new VideoResultDto();
            videoCheckDto.setReady(false);
            return JsonResultUtils.success(videoCheckDto);
        } else {
            VideoResultDto videoResultDto = videoService.mapVideoInfo2Dto(videoInfo);
            videoResultDto.setReady(true);
            return JsonResultUtils.success(videoResultDto);
        }
    }

    /**
     * 获取pc端的视频播放地址
     *
     * @param map
     */
    @PostMapping("getOnlineUrl")
    public Object getOnlineUrl(@RequestBody Map<String, String> map) {
        log.info("getOnlineUrl target url:{}", JSON.toJSONString(map));
        String targetUrl = map.get("targetUrl");
        return videoService.downLoadTargetUrl(Objects.requireNonNull(targetUrl));
    }

    /**
     * 获取用户允许发送视频信息，配置，用于客户端校验
     *
     * @param request
     * @return
     */
    @PostMapping("getUserVideoConfig")
    public Object getUserVideoConfig(HttpServletRequest request) {
        String curUser = Optional.ofNullable(UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME)).orElse("UNKNOW_");
        JsonResult<UserVideoConfig> userVideoConfig = videoService.getUserVideoConfig(curUser);
        log.info("getUserVideoConfig user [{}] userVideoConfig [{}]", curUser, JSON.toJSONString(userVideoConfig.getData()));
        return userVideoConfig;
    }

}
