package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.concurrent.CamelThreadFactory;
import com.qunar.qtalk.cricle.camel.common.config.VideoConfig;
import com.qunar.qtalk.cricle.camel.common.consts.ResourceTypeEnum;
import com.qunar.qtalk.cricle.camel.common.dto.VideoResultDto;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.store.swift.SwiftConfig;
import com.qunar.qtalk.cricle.camel.common.store.swift.SwiftStore;
import com.qunar.qtalk.cricle.camel.common.util.*;
import com.qunar.qtalk.cricle.camel.common.video.VideoParser;
import com.qunar.qtalk.cricle.camel.common.vo.UserVideoConfig;
import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;
import com.qunar.qtalk.cricle.camel.entity.VideoInfo;
import com.qunar.qtalk.cricle.camel.mapper.VideoInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VideoService {

    public static final String DEFAULT_TRANS_VIDEO_SIGN = "trans";

    public static final String DEFAULT_TRANS_VIDEO_FILE_TYPE = "mp4";

    @Resource
    private SwiftStore swiftStore;

    @Resource
    private SwiftConfig swiftConfig;

    @Resource
    private VideoInfoMapper videoInfoMapper;

    @Resource
    @Qualifier("ffmpegVideoParser")
    private VideoParser videoParser;

    @Value("${video_online_play_base_url}")
    private String onlinePlayBaseUrl;

    @Resource
    private VideoConfig videoConfig;

    @Resource
    private DozerUtils dozerUtils;

    private static final ThreadPoolExecutor FILE_DELETE_THREAD_POOL = new ThreadPoolExecutor(1, 1, 60L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new CamelThreadFactory("local_file_delete", true), new ThreadPoolExecutor.CallerRunsPolicy());

    public VideoInfo getVideoInfo(String videoMd5) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(videoMd5));
        return videoInfoMapper.getVideoInfoByMd5(videoMd5);
    }

    /**
     * 视频文件md5校验
     *
     * @param videoMd5
     * @return true:不存在，false：存在
     */
    public boolean videoMd5Check(String videoMd5) {
        return getVideoInfo(videoMd5) == null;
    }

    /**
     * 获取视频文件的信息
     * @param videoFile
     * @return
     */
    public VideoVo getVideoVo(File videoFile) {
        String absolutePath = videoFile.getAbsolutePath();
        return videoParser.parse(absolutePath, null);
    }

    /**
     * 判断是否需要转码
     * @param videoVo
     * @return
     */
    public boolean isNeedTrans(VideoVo videoVo) {
        return videoParser.isNeedTrans(videoVo);
    }

    /**
     * 具体转码方法
     *
     * @param videoFile
     * @return 转码后的文件
     */
    public File transVideo(File videoFile,VideoVo originVideoVo,boolean highDefinition) {
        String name = videoFile.getName();
        int index = StringUtils.lastIndexOf(name, ".");
        String prefixFileName = StringUtils.substring(name, 0, index);
        int transTime = 3;

        String originFilePath = FileUtils.getFilePath(videoFile);
        while (transTime-- > 0) {
            String transFileName = FileUtils.generateTransFileName(prefixFileName, DEFAULT_TRANS_VIDEO_SIGN, DEFAULT_TRANS_VIDEO_FILE_TYPE);

            String transFilePath = "";
            try {
                transFilePath = FileUtils.getSameDirectoryPathFilePath(videoFile, transFileName);
                videoParser.trans(originFilePath, null, transFilePath, originVideoVo, highDefinition);
                return new File(transFilePath);
            } catch (Exception e) {
                log.error("originFile [{}] transFile [{}] occur exception,current times [{}]",
                        originFilePath, transFilePath, transTime);
            }
        }
        return null;
    }

    /**
     * 获取视频文件首帧图片
     *
     * @param videoFile
     * @param videoVo
     * @return 图片文件
     */
    public File getFirstThumb(File videoFile, VideoVo videoVo) {
        String videoFileName = videoFile.getName();
        String thumbFileName = StringUtils.substring(videoFileName, 0, StringUtils.indexOf(videoFileName, ".")).concat(".png");
        String videoFilePath = FileUtils.getFilePath(videoFile);
        String thumbFilePath = String.join("/", FileUtils.getDirectoryPath(videoFile), thumbFileName);
        try {
            videoParser.getFirstThumb(videoFilePath, null, thumbFilePath, videoVo);
            return new File(thumbFilePath);
        } catch (Exception e) {
            log.error("getFirstThumb occur exception,videoFilePath [{}],thumbFilePath [{}]", videoFilePath, thumbFilePath, e);
        }
        return null;
    }

    /**
     * 保存视频信息
     *
     * @param originVideoVo 原始视频信息
     * @param transVideoVo  转码视频信息
     * @param thumb
     * @return
     */
    public VideoInfo saveVideoInfo(VideoVo originVideoVo, VideoVo transVideoVo, String thumb) {
        VideoInfo videoInfo = VideoInfo.builder()
                .resourceId(IDUtils.getUUIDWithType(ResourceTypeEnum.VIDEO.getType()))
                .originFilename(originVideoVo.getVideoName()).originFileMd5(originVideoVo.getVideoMd5())
                .transFilename(transVideoVo.getVideoName()).transFileMd5(transVideoVo.getVideoMd5()).firstThumb(thumb)
                .originFileInfo(originVideoVo).transFileInfo(transVideoVo).createTime(new Date()).build();
        videoInfoMapper.insertSelective(videoInfo);
        return videoInfo;
    }

    /**
     * 视频转码逻辑：
     * 判断原始文件md5,是否需要转码
     * 判断是否需要转码，
     * 转码生成本地文件
     * 取第一帧图片
     * <p>
     * 上传对象存储
     *
     * @param originalFile
     * @return
     */
    public JsonResult<VideoResultDto> doVideoTrans(File originalFile, Boolean highDefinition) {
        String md5 = Md5CaculateUtil.getMD5(originalFile);
        log.info("newFile:{},md5:{}", originalFile.getAbsolutePath(), md5);
        VideoInfo videoInfo = getVideoInfo(md5);
        if (videoInfo == null) {
            boolean upload = swiftStore.upload(originalFile);
            if (upload) {
                VideoVo originVideoVo = getVideoVo(originalFile);
                boolean needTrans = isNeedTrans(originVideoVo);
                if (needTrans) {
                    File transVideo = transVideo(originalFile, originVideoVo, highDefinition);
                    if (Objects.isNull(transVideo)) {
                        return JsonResultUtils.fail(BaseCode.ERR_FILE_TRANS_FAIL, null);
                    }
                    boolean upload1 = swiftStore.upload(transVideo);
                    if (!upload1) {
                        return JsonResultUtils.fail(BaseCode.ERR_UPLOAD_SWIFT_FAIL, null);
                    }
                    VideoVo transVideoVo = getVideoVo(transVideo);
                    File firstThumb = getFirstThumb(transVideo, transVideoVo);
                    boolean upload2 = swiftStore.upload(firstThumb);
                    try {
                        videoInfo = saveVideoInfo(originVideoVo, transVideoVo, firstThumb.getName());
                    } catch (Exception e) {
                        log.error("save video info occur exception,originVideo:{},transVideoVo:{}", JSON.toJSONString(originVideoVo), JSON.toJSONString(transVideoVo), e);
                        return JsonResultUtils.fail(500, "处理失败", null);
                    }
                    if (upload && upload1 && upload2) {
                        FILE_DELETE_THREAD_POOL.execute(() -> {
                            try {
                                originalFile.delete();
                                transVideo.delete();
                                firstThumb.delete();
                                log.info("delete files success,originalFile:{},transVideo:{},firstThumb:{}", originalFile.getAbsolutePath(), transVideo.getAbsolutePath(), firstThumb.getAbsolutePath());
                            } catch (Exception e) {
                                log.error("delete files fail,originalFile:{},transVideo:{},firstThumb:{}", originalFile.getAbsolutePath(), transVideo.getAbsolutePath(), firstThumb.getAbsolutePath(), e);
                            }
                        });
                    }
                } else {
                    // 如果不需要转码
                    File firstThumb = getFirstThumb(originalFile, originVideoVo);
                    boolean upload2 = swiftStore.upload(firstThumb);
                    videoInfo = saveVideoInfo(originVideoVo, originVideoVo.copy(), firstThumb.getName());
                    if (upload2) {
                        FILE_DELETE_THREAD_POOL.execute(() -> {
                            try {
                                originalFile.delete();
                                firstThumb.delete();
                                log.info("delete files success,originalFile:{},firstThumb:{}", originalFile.getAbsolutePath(), firstThumb.getAbsolutePath());
                            } catch (Exception e) {
                                log.error("delete files fail,originalFile:{},firstThumb:{}", originalFile.getAbsolutePath(), firstThumb.getAbsolutePath(), e);
                            }
                        });

                    }
                }
            }
        } else {
            FILE_DELETE_THREAD_POOL.execute(() -> {
                try {
                    originalFile.delete();
                    log.error("delete files success,originalFile:{}", originalFile.getAbsolutePath());
                } catch (Exception e) {
                    log.error("delete files fail,originalFile:{}", originalFile.getAbsolutePath(), e);
                }
            });
        }

        VideoResultDto videoResultDto = mapVideoInfo2Dto(videoInfo);
        return JsonResultUtils.success(videoResultDto);
    }

    public VideoResultDto mapVideoInfo2Dto(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return null;
        }
        VideoResultDto videoResultDto = dozerUtils.map(videoInfo, VideoResultDto.class);
        videoResultDto.setOriginUrl(swiftConfig.getSwiftBaseHttpUrl().concat(videoResultDto.getOriginFilename()));
        videoResultDto.setTransUrl(swiftConfig.getSwiftBaseHttpUrl().concat(videoResultDto.getTransFilename()));
        videoResultDto.setFirstThumbUrl(swiftConfig.getSwiftBaseHttpUrl().concat(videoResultDto.getFirstThumb()));
        videoResultDto.setOnlineUrl(onlinePlayBaseUrl.concat(videoResultDto.getTransUrl()));
        return videoResultDto;
    }

    /**
     * 下载pc端视频，转码返回
     *
     * @param targetUrl
     * @return
     */
    public JsonResult<VideoResultDto> downLoadTargetUrl(String targetUrl) {
        String localFileName = FileUtils.generateLocalSysFileName(targetUrl);
        File newFile = new File(swiftConfig.getTempDirectory(), localFileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            HttpClientUtils.downLoadFile(targetUrl, fileOutputStream);
            log.info("download file over,targetUrl:{},localFileName:{},fileSize:{} byte", targetUrl, newFile.getAbsolutePath(), newFile.length());
            String md5 = Md5CaculateUtil.getMD5(newFile);
            VideoInfo videoInfo = getVideoInfo(md5);
            VideoResultDto videoResultDto;
            if (videoInfo != null) {
                newFile.delete();
                log.info("targetUrl:{} md5 check is exists,delete local temp file:{}", targetUrl, newFile.getAbsolutePath());
                videoResultDto = mapVideoInfo2Dto(videoInfo);
            } else {
                JsonResult<VideoResultDto> videoResultDtoJsonResult = doVideoTrans(newFile, false);
                if (!videoResultDtoJsonResult.isRet()) {
                    return videoResultDtoJsonResult;
                }
                videoResultDto = videoResultDtoJsonResult.getData();
            }
            videoResultDto.setReady(true);
            return JsonResultUtils.success(videoResultDto);
        } catch (Exception e) {
            log.error("downLoadTargetUrl occur exception,targetUrl:{}", targetUrl, e);
        }
        return JsonResultUtils.fail(500, "处理失败", null);
    }

    public JsonResult<UserVideoConfig> getUserVideoConfig(String user) {
        return JsonResultUtils.success(UserVideoConfig.builder().videoTimeLen(videoConfig.getVideoTimeLen(user) * 1000L).
                videoFileSize(videoConfig.getVideoFileSize(user) * 1024 * 1024L).highDefinition(videoConfig.getVideoHighDefinition(user)).build());
    }
}
