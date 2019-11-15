package com.qunar.qtalk.cricle.camel.common.consts;

/**
 * QmoConsts
 *
 * @author binz.zhang
 * @date 2019/1/21
 */
public class QmoConsts {

    public static final String COMMENT_FAIL_NO_POST = "comment.fail.postNotExist";
    public static final String COMMENT_FAIL_PARAM_ILLEGAL = "comment.fail.paramIllegal";
    public static final String COMMENT_UPDATE_LIKENUM_FAIL = "comment.fail.updateLikeNum";
    public static final String COMMENT_SQL_EXCEPTION = "comment.exception.sqlException";
    public static final String COMMENT_REDIS_EXCEPTION = "comment.exception.redisException";
    public static final String COMMENT_UNKNOWN_EXCEPTION = "comment.exception.unKnownException";

    public static final String ANONYMOUSE_NULL = "anonymouse.getFromDBIsNull";

    public static final String LIKE_SAVE_ERROR = "like.fail.saveDB";
    public static final String LIKE_GET_POST_LIKENUM_ERROR = "like.fail.getPostLikeNum";
    public static final String LIKE_GET_COMMENT_LIKENUM_ERROR = "like.fail.getCommentLikeNum";


    public static final String POST_NOT_EXIST = "post.notExist";
    public static final String POST_GET_FROM_DB_FAIL = "post.fail.getFromDB";
    public static final String POST_UPDATE_COMMENT_NUM_FAIL = "post.fail.updateCommentNum";
    public static final String POST_UPDATE_LIKE_NUM_FAIL = "post.fail.updateLikeNum";
    public static final String POST_DELETE_FAIL = "post.fail.delete";
    public static final String POST_MANAGE_SEARCH_FAIL = "post.fail.manage.search";
    public static final String POST_GETTOP_FAIL = "post.fail.getTopPost";
    public static final String POST_GETHOT_FAIL = "post.fail.getHotPost";
    public static final String POST_SET_TOP_OR_HOT_FAIL = "post.fail.setTopOrHot";


    public static final String MESSAGE_GET_LIST_FAIL = "message.fail.getMsgList";
    public static final String MESSAGE_UPDATE_READFLAG_FAIL = "message.fail.updateReadFlag";


    public static final String SYSTEM_ERROR = "system_error";

    public static final String REDIS_COST_MILLIS_TIME = "redis_costTime";
    public static final String GET_USERS_FROM_PG = "get_users_from_pg";
    public static final String GET_USERS_FROM_REIDS = "get_users_from_redis";


    /**
     * request相关监控
     */
    public static final String REQUEST = "request";

    public static final String REQUEST_POST = "request.post";
    public static final String REQUEST_POST_UPLOAD = "request.post.upload";

    public static final String REQUEST_LIKE = "request.like";

    public static final String REQUEST_MESSAGE = "request.message";

    public static final String REQUEST_COMMENT_V1 = "request.comment.V1";
    public static final String REQUEST_COMMENT_UPLOAD_V1 = "request.comment.upload.V1";

    public static final String REQUEST_COMMENT_V2 = "request.comment.V2";
    public static final String REQUEST_COMMENT_UPLOAD_V2 = "request.comment.upload.V2";
    /**
     * sendNotify 相关监控
     */
    public static final String SEND_NOTIFY_FAIL = "send_notify_fali";

}
