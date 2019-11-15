package com.qunar.qtalk.cricle.camel.common.consts;

import java.util.TreeSet;

/**
 * Created by haoling.wang on 2018/12/28.
 * <p>
 * 系统常量
 */
public class ContextConsts {

    public static final String SYSTEM_NAME = "cricle_camel";

    public static final String MDC_URI = "mdc_uri";

    public static final String MDC_TRACE_ID = "mdc_trace_id";

    public static final String MDC_USER = "mdc_user";

    public static final String MDC_START_TIME = "mdc_start_time";

    /**
     * 新发帖，push presence 时的内容长度限制
     */
    public static final Integer POST_NOTIFY_LIMIT = 200;

    /**
     *
     */
    public static final String KAFKA_SENDNOTIFY_KEY = "sendNotify";
    public static final String KAFKA_SENDMESSAGE_KEY = "sendMessage";

    public static final Integer HOT_COMMENT_THRESHOLD = 3; //热门回复的阈值 点赞超过3次的可称之为热门评论

    public static final int POST_TYPE_NORMAL = 1;
    public static final int POST_TYPE_TOP = 2;
    public static final int POST_TYPE_HOT = 4;


    /**
     * AtList @列表的分隔符
     */
    public static final String AT_LIST_SPLIT_SIGN = ",";


    public static final int[] REATAIN_DATE = new int[]{1, 2,3,4,5,6,7,14,30,60,90};

}
