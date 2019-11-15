package com.qunar.qtalk.cricle.camel.common.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class Base64Utils {

    public static byte[] encode(byte[] bytes) {
        return Base64.encodeBase64(bytes);
    }

    public static byte[] decode(String base64Str) {
        return Base64.decodeBase64(base64Str);
    }

}
