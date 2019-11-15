package com.qunar.qtalk.cricle.camel.common.util;

import java.util.concurrent.ThreadLocalRandom;

public class StrRandomUtil {

    private static final String[] DIC_LOWERCASE = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z"};

    private static final String[] DIC_UPPERCASE = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private static final String[] DIC_NUMBER = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    private static final String[] DIC_SIGN = {
            "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
            "_", "+", "`", "-", "=", "{", "}", "|", ":", "\"", "<",
            ">", "?", "[", "]", "\\", ";", "'", ",", ".", "/"};

    private static final String[][] DIC_INDEX = {
            DIC_LOWERCASE, DIC_UPPERCASE, DIC_NUMBER,
            DIC_SIGN};

    private static final String[][] EASY_DIC_INDEX = {
            DIC_LOWERCASE, DIC_UPPERCASE, DIC_NUMBER};

    private static int randomInt(int pos) {
        return ThreadLocalRandom.current().nextInt(pos);
    }

    public static String genStrRandom(int len) {
        StringBuilder buff = new StringBuilder(len);
        while (len-- > 0) {
            String[] dic = DIC_INDEX[randomInt(DIC_INDEX.length)];
            buff.append(dic[randomInt(dic.length)]);
        }
        return buff.toString();
    }

    public static String genEasyStrRandom(int len) {
        StringBuilder buff = new StringBuilder(len);
        while (len-- > 0) {
            String[] dic = EASY_DIC_INDEX[randomInt(EASY_DIC_INDEX.length)];
            buff.append(dic[randomInt(dic.length)]);
        }
        return buff.toString();
    }

}