package com.qunar.qtalk.cricle.camel.common.util;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;

@Slf4j
public class FileUtils {

    public static final String SYS_SEPARATOR = File.separator;

    public static final String TEMP_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");

    public static final String USER_DIRECTORY_PATH = System.getProperty("user.home");


    /**
     * 生成本地临时文件，确保系统唯一
     *
     * @param originFileName
     * @return
     */
    public static String generateFileName(String originFileName) {
        return Joiner.on("_")
                .join(new DateTime().toString("yyyyMMddHHmmssSSS"), StrRandomUtil.genEasyStrRandom(6), originFileName);
    }

    /**
     * 生成转码文件名称
     *
     * @param originFilePrefixName
     * @param fileSign
     * @param fileType
     * @return
     */
    public static String generateTransFileName(String originFilePrefixName, String fileSign, String fileType) {
        return originFilePrefixName.concat("_").concat(fileSign).concat("_")
                .concat(StrRandomUtil.genEasyStrRandom(1)).concat(".").concat(fileType);
    }

    public static String getSysTempDirectory(String subDirectory) {
        String tempDirectory = String.join("/", USER_DIRECTORY_PATH, subDirectory);
        File file = new File(tempDirectory);
        if (!file.exists()) {
            file.mkdir();
        }
        log.info("getSysTempDirectory:[{}]", tempDirectory);
        return tempDirectory;
    }

    public static String getFilePrefix(String input) {
        input = input.trim();
        if (StringUtils.endsWith(input, SYS_SEPARATOR)) {
            input = StringUtils.substring(input, 0, input.length() - 1);
        }
        if (StringUtils.contains(input, SYS_SEPARATOR)) {
            input = StringUtils.substring(input, StringUtils.lastIndexOf(input, SYS_SEPARATOR) + 1);
        }

        return StringUtils.substring(input, 0,StringUtils.lastIndexOf(input, "."));
    }

    public static String getFileSuffix(String input) {
        return StringUtils.substring(input, StringUtils.lastIndexOf(input, ".") + 1);
    }

    /**
     * 获取文件的绝对路径
     * @param file
     * @return
     */
    public static String getFilePath(File file) {
        return file.getAbsolutePath();
    }

    /**
     * 获取文件所在路径
     *
     * @param file
     * @return
     */
    public static String getDirectoryPath(File file) {
        String filePath = getFilePath(file);
        return StringUtils.substring(filePath, 0, StringUtils.lastIndexOf(filePath, SYS_SEPARATOR));
    }

    public static String getSameDirectoryPathFilePath(File file, String newFileName) throws FileExistsException {
        String newFilePath = getDirectoryPath(file).concat(SYS_SEPARATOR).concat(newFileName);
        if ((new File(newFilePath)).exists()) {
            throw new FileExistsException(String.format("directory %s is exists,please check", newFilePath));
        }
        return newFilePath;
    }

    /**
     * 生成系统本地文件（源文件）
     *
     * @param originalFilename
     * @return
     */
    public static String generateLocalSysFileName(String originalFilename) {
        String filePrefix = FileUtils.getFilePrefix(originalFilename);
        String fileSuffix = FileUtils.getFileSuffix(originalFilename);
        String handlefilePrefix = StringUtils.length(filePrefix) > 30 ?
                (StringUtils.substring(filePrefix, 0, 30)) : filePrefix;
        String handleOriginFileName = handlefilePrefix.concat(".").concat(fileSuffix);
        String fileName = FileUtils.generateFileName(handleOriginFileName);
        return fileName;
    }

    public static void main(String[] args) {
//        File file = new File("/Users/whling/Downloads");
//        System.out.println(getFilePath(file));
//
//        System.out.println(getDirectoryPath(file));
//
//        System.out.println(SYS_SEPARATOR);
//        System.out.println(TEMP_DIRECTORY_PATH);
//        System.out.println(USER_DIRECTORY_PATH);
//
//        System.out.println(getFileSuffix("/Users/whling/Downloads/SampleVideo_1280x720_1mb.mp4"));
//        System.out.println(getFilePrefix("/Users/whling/Downloads/SampleVideo_1280x720_1mb.mp4"));
//
//        System.out.println(generateLocalSysFileName("/fjdadslf/fjlads/aa.mp4"));
    }

}
