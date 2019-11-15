package com.qunar.qtalk.cricle.camel;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class FfmpegTest {

    public static final String FFMPEG_COMMAND_TEMPLATE
            = "ffmpeg -i %s -b:v  640k %s";

    public static void main(String[] args) {

        Runtime runtime = Runtime.getRuntime();
        try {
            String origin = args[0];
            String to = args[1];
            Process process = runtime.exec(String.format(FFMPEG_COMMAND_TEMPLATE, origin, to));
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            System.out.println("OUTPUT");
            while ((line = stdoutReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("ERROR");
            while ((line = stderrReader.readLine()) != null) {
                System.out.println(line);
            }
            int exitVal = process.waitFor();
            System.out.println("process exit value is " + exitVal);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void md5() throws Exception {
        Stopwatch started = Stopwatch.createStarted();
        FileInputStream fileInputStream1 = new FileInputStream(new File(".mp4"));
        String md51 = DigestUtils.md5DigestAsHex(fileInputStream1);
        System.out.println(md51);
        System.out.println(started.elapsed(TimeUnit.MILLISECONDS));

        FileInputStream fileInputStream2 = new FileInputStream(new File(".3gp"));
        String md52 = DigestUtils.md5DigestAsHex(fileInputStream1);
        System.out.println(md52);
        System.out.println(started.elapsed(TimeUnit.MILLISECONDS));


        FileInputStream fileInputStream3 = new FileInputStream(new File(".mp4"));
        String md53 = DigestUtils.md5DigestAsHex(fileInputStream3);
        System.out.println(md53);
        String md54 = DigestUtils.md5DigestAsHex(fileInputStream3);
        System.out.println(md54);
        System.out.println(started.elapsed(TimeUnit.MILLISECONDS));
    }


}
