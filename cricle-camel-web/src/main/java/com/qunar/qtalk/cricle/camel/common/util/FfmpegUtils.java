package com.qunar.qtalk.cricle.camel.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.exception.VideoParseException;
import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ffmpeg工具
 */
public class FfmpegUtils {

    private static final Logger log = LoggerFactory.getLogger(FfmpegUtils.class);

    public static final Integer DEFAULT_FIRST_THUMB_HEIGHT = 720;

    public static final Integer DEFAULT_FIRST_THUMB_WIDTH = 1280;

    public static final String REGEX_VIDEO = "Video: (.*?), (.*?), (.*?)[,\\s]";

    public static final String REGEX_VIDEO_SIZE = "[\\s|,]+(\\d{1,})x(\\d{1,})[\\s|,]+";

    public static final String REGEX_VIDEO_DURATION = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";

    public static final String REGEX_VIDEO_RATATE = "rotate[\\s]+: (\\d{1,})";

    public static final String FFMPEG_COMMAND_TEMPLATE = "%s -i %s -vcodec libx264 -strict -2 -vf scale=%@% %s";

    public static final String FFMPEG_QUICK_TRANS_COMMAND_TEMPLATE = "%s -i %s -vcodec copy -acodec copy %s";

    public static final String FFMPEG_HIGH_DEFINITION_TRANS_COMMAND_TEMPLATE = "%s -i %s -vcodec libx264 -q:v 1 %s";

    public static final String FFMPEG_QUICK_HIGH_DEFINITION_TRANS_COMMAND_TEMPLATE = "%s -i %s -vcodec copy -acodec copy -q:v 1 %s";

    public static final Pattern P_V = Pattern.compile(REGEX_VIDEO);

    public static final Pattern P_V_S = Pattern.compile(REGEX_VIDEO_SIZE);

    public static final Pattern P_V_D = Pattern.compile(REGEX_VIDEO_DURATION);

    public static final Pattern P_V_R = Pattern.compile(REGEX_VIDEO_RATATE);

    public static void main(String[] args) throws Exception {
//        getThumb("ffmpeg", "/Users/whling/Downloads/SampleVideo_1280x720_1mb.mp4",
//                "/Users/whling/Downloads/SampleVideo_1280x720_1mb6.png",
//                1280, 720, 0, 0, 0);

//        trans("ffmpeg",
//                "/Users/whling/Downloads/SampleVideo_176x144_1mb.3gp","/Users/whling/Downloads/SampleVideo_176x144_1mb_test777.mp4");
//
//        System.out.println("over");

//        long timelen = getTimelen("00:00:03.14");
//        System.out.println(timelen);

        String str = "ffmpeg version 4.1.4 Copyright (c) 2000-2019 the FFmpeg developers\n" +
                "  built with Apple LLVM version 10.0.1 (clang-1001.0.46.4)\n" +
                "  configuration: --prefix=/usr/local/Cellar/ffmpeg/4.1.4_1 --enable-shared --enable-pthreads --enable-version3 --enable-avresample --cc=clang --host-cflags='-I/Library/Java/JavaVirtualMachines/adoptopenjdk-12.0.1.jdk/Contents/Home/include -I/Library/Java/JavaVirtualMachines/adoptopenjdk-12.0.1.jdk/Contents/Home/include/darwin' --host-ldflags= --enable-ffplay --enable-gnutls --enable-gpl --enable-libaom --enable-libbluray --enable-libmp3lame --enable-libopus --enable-librubberband --enable-libsnappy --enable-libtesseract --enable-libtheora --enable-libvorbis --enable-libvpx --enable-libx264 --enable-libx265 --enable-libxvid --enable-lzma --enable-libfontconfig --enable-libfreetype --enable-frei0r --enable-libass --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-libopenjpeg --enable-librtmp --enable-libspeex --enable-videotoolbox --disable-libjack --disable-indev=jack --enable-libaom --enable-libsoxr\n" +
                "  libavutil      56. 22.100 / 56. 22.100\n" +
                "  libavcodec     58. 35.100 / 58. 35.100\n" +
                "  libavformat    58. 20.100 / 58. 20.100\n" +
                "  libavdevice    58.  5.100 / 58.  5.100\n" +
                "  libavfilter     7. 40.101 /  7. 40.101\n" +
                "  libavresample   4.  0.  0 /  4.  0.  0\n" +
                "  libswscale      5.  3.100 /  5.  3.100\n" +
                "  libswresample   3.  3.100 /  3.  3.100\n" +
                "  libpostproc    55.  3.100 / 55.  3.100\n" +
                "Input #0, mov,mp4,m4a,3gp,3g2,mj2, from 'SampleVideo_176x144_1mb_1234.mp4':\n" +
                "  Metadata:\n" +
                "    major_brand     : isom\n" +
                "    minor_version   : 512\n" +
                "    compatible_brands: isomiso2avc1mp41\n" +
                "    encoder         : Lavf58.20.100\n" +
                "  Duration: 00:00:40.83, start: 0.000000, bitrate: 568 kb/s\n" +
                "    Stream #0:0(und): Video: h264 (High) (avc1 / 0x31637661), yuv420p, 176x144 [SAR 12:11 DAR 4:3], 531 kb/s, 15 fps, 15 tbr, 15360 tbn, 30 tbc (default)\n" +
                "    Metadata:\n" +
                "      handler_name    : VideoHandler\n" +
                "    Stream #0:1(und): Audio: aac (LC) (mp4a / 0x6134706D), 8000 Hz, mono, fltp, 37 kb/s (default)\n" +
                "    Metadata:\n" +
                "      handler_name    : SoundHandler";

        String str1 = "Stream #0:0(eng): Video: h264 (Baseline) (avc1 / 0x31637661), yuvj420p(pc, smpte170m), 1280x720, 8628 kb/s, SAR 1:1 DAR 16:9, 29.95 fps, 30 tbr, 90k tbn, 180k tbc (default)";


//        String s = "";
//        Matcher matcher = P_V.matcher(str);
//
//        String regexVideo1 = ",\\s*(\\d{1,})x(\\d{1,}),";
//        String regexVideo2 = "Video: ";
//
//        Pattern P_V_R = Pattern.compile(regexVideo1);

        Matcher matcher1 = P_V_S.matcher(str1);
//        if (matcher1.find()) {
//            System.out.println(matcher.group(1));
//            String DPI = matcher.group(3);
//            String[] xes = DPI.split("x");
//            System.out.println(Arrays.toString(xes));
////            System.out.println(" " + videoType + " " + matcher.group(2) + " " + matcher.group(3));
//        }

        if (matcher1.find()) {
            System.out.println(matcher1.group(1));
            System.out.println(matcher1.group(2));
        }
    }


    /****
     * 获取指定时间内的图片
     * @param videoFilename:视频路径
     * @param thumbFilename:图片保存路径
     * @param width:图片长
     * @param height:图片宽
     * @param hour:指定时
     * @param min:指定分
     * @param sec:指定秒
     * @throws IOException
     * @throws InterruptedException
     */
    public static void getThumb(String ffmpegApp, String videoFilename, String thumbFilename, int width,
                                int height, int hour, int min, float sec) throws IOException,
            InterruptedException {
        BufferedReader br = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegApp,
                    "-i", videoFilename, "-y", "-f", "mjpeg", "-ss", hour + ":" + min
                    + ":" + sec, "-vframes", "1", thumbFilename);
            Process process = processBuilder.start();
            log.info("getThumb command:{}", Joiner.on(" ").join(processBuilder.command()));
            System.out.println(Joiner.on(" ").join(processBuilder.command()));
            InputStream stderr = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static int getVideoTime(String video_path, String ffmpeg_path) {
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(video_path);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String videoInfo = sb.toString();

            String regexVideo = "Video: (.*?), (.*?), (.*?)[,\\s]";
            String regexVideo1 = "Video: (.*?)(\\d+x\\d+) (.*?)[,\\s]";
            Pattern pV
                    = Pattern.compile(regexVideo);
            Matcher matcher = pV.matcher(videoInfo);
            if (matcher.find()) {
                System.out.println(" " + matcher.group(1) + " " + matcher.group(2) + " " + matcher.group(3));
            }

            String regexAudio = "Audio: (\\w*), (\\d*) Hz";
            Pattern pA
                    = Pattern.compile(regexAudio);
            Matcher matcherA = pA.matcher(videoInfo);
            if (matcher.find()) {
                System.out.println(" " + matcherA.group(1) + " " + matcherA.group(2) + " " + matcherA.group(3));
            }

            //从视频信息中解析时长
//            Duration: 00:00:03.14, start: 0.000000, bitrate: 10371 kb/s
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(videoInfo);
            if (m.find()) {
                int time = 0;//getTimelen(m.group(1));
                log.info(video_path + ",视频时长：" + time + ",duration:" + m.group(1) + ", 开始时间：" + m.group(2) + ",比特率：" + m.group(3) + "kb/s");
                return time;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static VideoVo getVideoVo(String video_path, String ffmpeg_path) {
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(video_path);
        String videoType = "", width = "", height = "", duration = "", bitRate = "";
        boolean rotate = false;

        BufferedReader br = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process p = builder.start();

            //从输入流中读取视频信息
            br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String videoInfo = sb.toString();
            Matcher videoMatcher = P_V.matcher(videoInfo);

            if (videoMatcher.find()) {
                videoType = videoMatcher.group(1);
                System.out.println(" " + videoType + " " + videoMatcher.group(2) + " " + videoMatcher.group(3));
            }

            Matcher videoRotateMatcher = P_V_R.matcher(videoInfo);
            if (videoRotateMatcher.find()) {
                String rotateStr = StringUtils.trim(videoRotateMatcher.group(1));
                if (StringUtils.equalsAnyIgnoreCase(rotateStr, "90", "270")) {
                    log.info("video [{}],rotate [{}],isRotate [{}]", video_path, rotateStr, rotate);
                    rotate = true;
                }
            }

            Matcher videoSizeMatcher = P_V_S.matcher(videoInfo);
            if (videoSizeMatcher.find()) {
                width = videoSizeMatcher.group(1);
                height = videoSizeMatcher.group(2);
                if (rotate) {
                    String temp = width;
                    width = height;
                    height = temp;
                }
                log.info("video [{}],width [{}],height [{}],rotate [{}]", video_path, width, height, rotate);
            }

//            String regexAudio = "Audio: (\\w*), (\\d*) Hz";
//            Pattern pA = Pattern.compile(regexAudio);
//            Matcher matcherA = pA.matcher(videoInfo);
//            if (matcherA.find()) {
//                System.out.println(" " + matcherA.group(1) + " " + matcherA.group(2) + " " + matcherA.group(3));
//            }

            /**
             * 从视频信息中解析时长
             */
            Matcher videoDurationMatcher = P_V_D.matcher(videoInfo);
            if (videoDurationMatcher.find()) {

                duration = videoDurationMatcher.group(1);
                bitRate = videoDurationMatcher.group(3);
                log.info("video [{}],duration [{}],startTime,[{}],bitRate [{}] kb/s", video_path, duration, videoDurationMatcher.group(2), bitRate);
            }
        } catch (Exception e) {
            log.error("getVideoVo occur exception,video_path:{},ffmpeg_path:{}", video_path, ffmpeg_path, e);
        } finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        File file = new File(video_path);
        return VideoVo.builder()
                .videoSuffix(FileUtils.getFileSuffix(video_path))
                .videoName(file.getName()).videoMd5(Md5CaculateUtil.getMD5(file))
                .videoSize(file.length())
                .videoType(videoType).width(width).height(height).duration(getTimelen(duration)).bitRate(bitRate).build();
    }

    //格式:"00:00:10.68"
    private static long getTimelen(String timelen) {
        try {
            log.info("timelen:{}", timelen);
            long min = 0;
            timelen = timelen.trim();
            String strs[] = timelen.split(":");
            log.info("timelen strs:{}", Arrays.toString(strs));
            if (strs[0].compareTo("0") > 0) {
                min += Integer.valueOf(strs[0]) * 60 * 60 * 1000;//秒
            }
            if (strs[1].compareTo("0") > 0) {
                min += Integer.valueOf(strs[1]) * 60 * 1000;
            }
            if (strs[2].compareTo("0") > 0) {
                min += Float.valueOf(strs[2]) * 1000;
            }
            return min;
        } catch (Exception e) {
            log.error("get video timeLen occur exception,timelen:{}", timelen, e);
        }
        return 0L;
    }

    public static void trans(String ffmpegPath, String originVideoPath, String newVideoPath, VideoVo originVideoVo, boolean quickTrans, boolean isHighDefinition) {
        Runtime runtime = Runtime.getRuntime();
        String height = originVideoVo.getHeight();
        String width = originVideoVo.getWidth();
        String temp = DEFAULT_FIRST_THUMB_WIDTH + ":" + DEFAULT_FIRST_THUMB_HEIGHT;
        if (!Strings.isNullOrEmpty(height) && !Strings.isNullOrEmpty(width) && NumberUtils.compare(Integer.valueOf(height), Integer.valueOf(width)) > 0) {
            temp = DEFAULT_FIRST_THUMB_HEIGHT + ":" + DEFAULT_FIRST_THUMB_WIDTH;
        }
        log.info("default ffmpeg command size:[{}]", temp);
        String ffmpegCommandTemplate = FFMPEG_COMMAND_TEMPLATE;
        String commandTemplate = StringUtils.replace(ffmpegCommandTemplate, "%@%", temp);
        if (quickTrans) {
            commandTemplate = FFMPEG_QUICK_TRANS_COMMAND_TEMPLATE;
            if (isHighDefinition) {
                commandTemplate = FFMPEG_QUICK_HIGH_DEFINITION_TRANS_COMMAND_TEMPLATE;
            }
        } else {
            if (isHighDefinition) {
                commandTemplate = FFMPEG_HIGH_DEFINITION_TRANS_COMMAND_TEMPLATE;
            }
        }
        String commandStr = String.format(commandTemplate, ffmpegPath, originVideoPath, newVideoPath);
        log.info("trans quickTrans:[{}],isHighDefinition:{{}},command:[{}]", quickTrans, isHighDefinition, commandStr);
        Process process = null;
        try {
            process = runtime.exec(commandStr);
        } catch (Exception e) {
            log.error("runtime exec commandStr:{} occur exception", commandStr, e);
            throw new VideoParseException(e);
        }
        try (
                BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        ) {
            String line;
            log.info(String.format("====EXEC COMMAND:[%s]", commandStr));
            log.info("OUTPUT");
            while ((line = stdoutReader.readLine()) != null) {
                log.info(line);
            }
            log.info("ERROR");
            while ((line = stderrReader.readLine()) != null) {
                log.info(line);
            }
            int exitVal = process.waitFor();
            log.info("process exit value is " + exitVal);
        } catch (IOException e) {
            log.error("trans exception,originVideoPath:{},newVideoPath:{}", originVideoPath, newVideoPath);
            throw new VideoParseException(e);
        } catch (InterruptedException e) {
            log.error("trans exception,originVideoPath:{},newVideoPath:{}", originVideoPath, newVideoPath);
            throw new VideoParseException(e);
        }

    }



}
