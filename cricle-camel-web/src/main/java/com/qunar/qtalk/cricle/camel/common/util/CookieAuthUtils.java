package com.qunar.qtalk.cricle.camel.common.util;

import com.google.common.base.Splitter;
import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Slf4j
public class CookieAuthUtils {

    public static final String KEY_USER_NAME = "u";

    public static final String KEY_USER_DOMAIN = "d";

    public static final String KEY_REQUEST_TIME = "t";

    public static final String CKEY_SPLIT = "&";

    public static final String CKEY_KEY_JOINER = "=";

    /**
     *
     * @param cKey
     * @return
     */
    public static Map<String, String> getUserFromCKey(String cKey) {
        String decodeCkey = new String(Base64Utils.decode(cKey));
        log.debug("decode cKey is {}", decodeCkey);
        return Splitter.on(CKEY_SPLIT).trimResults().withKeyValueSeparator(CKEY_KEY_JOINER).split(decodeCkey);
    }

    public static String getCurrentUser() {
        return UserHolder.getValue(KEY_USER_NAME);
    }
}
