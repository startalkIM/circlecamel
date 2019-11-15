package com.qunar.qtalk.cricle.camel.common.consts;

/**
 * Created by haoling.wang on 2019/1/4.
 */
public class RedisConsts {

    public static final String REDIS_SEPERATE = ":";

    public static final String REDIS_PREFIX = "cricle_camel".concat(REDIS_SEPERATE);

    public static final String REDIS_MAX_COUNT_TOP_OR_HOT = "maxCountTopOrHot";

    public static final String REDIS_LIKE = "%s:like:%s";

    public static final String REDIS_COMMENT_LIKE_RANK = "post:comment_rank:%s";

    public static final String REDIS_COMMENT_LIKE_RANK_V2 = "post:comment_rank_v2:%s";

    /** post置顶帖集合 **/
    public static final String REDIS_TOP_POST = "post:top:zset";

    /** post置热帖集合 **/
    public static final String REDIS_HOT_POST = "post:hot:zset";

    /** post置顶帖的置顶时间 **/
    public static final String REDIS_TOP_POST_WITH_EXPIRE = "post:top:%s";

    /** post置热帖的置热时间 **/
    public static final String REDIS_HOT_POST_WITH_EXPIRE = "post:hot:%s";

    public static String getRedisCommentLikeRankKey(String postUUID) {
        return String.format(REDIS_COMMENT_LIKE_RANK, postUUID);
    }
    /** 帖子最热评论 **/
    public static String getRedisCommentLikeRankKeyV2(String postUUID) {
        return String.format(REDIS_COMMENT_LIKE_RANK_V2, postUUID);
    }

    public static String getRedisTopPostWithExpire(String postUUID) {
        return String.format(REDIS_TOP_POST_WITH_EXPIRE, postUUID);
    }
    public static String getRedisHotPostWithExpire(String postUUID) {
        return String.format(REDIS_HOT_POST_WITH_EXPIRE, postUUID);
    }
}
