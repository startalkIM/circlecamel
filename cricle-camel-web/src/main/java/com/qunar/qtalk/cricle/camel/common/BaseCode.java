package com.qunar.qtalk.cricle.camel.common;

/**
 * Created by haoling.wang on 2018/12/28.
 */
public enum BaseCode {

    OK(200, "操作成功"),
    ERROR(500, "服务器异常"),
    DB_ERROR(501, "数据库操作异常"),
    NO_PERMISSION(505,"没有权限操作"),
    BADREQUEST(400, "参数不合法"),
    OP_RESOURCE_NOTFOUND(404, "操作资源不存在"),
    OP_NOT_SUPPORT(405, "不支持的操作"),

    // 帖子相关错误:21 评论相关错误:22
    ERR_POST_IS_ALREADY_TOP(21001, "该帖子已经是置顶贴了"),
    ERR_POST_IS_NOT_TOP(21002, "该帖子不是置顶帖"),
    ERR_POST_SET_TOP(21003, "置顶帖子失败，稍后重试"),
    ERR_POST_NOT_SET_TOP(21004, "取消置顶帖子失败，稍后重试"),
    ERR_POST_DELETE_FLAG_REPEAT_WITH_OP(21005, "帖子状态与操作状态一致,操作失败"),
    ERR_POST_IS_ALREADY_HOT(21006, "该帖子已经是热贴了"),
    ERR_POST_IS_ALREADY_TOP_OR_HOT(21007, "该帖子已经置顶/热了"),
    ERR_POST_IS_NOT_HOT(21008, "该帖子不是置热帖"),
    ERR_POST_NOT_SET_HOT(21009, "取消置热帖子失败，稍后重试"),
    ERR_POST_TOP_OR_HOT_IS_OVER(21010, "置顶/热帖子条数不能超过%s条"),
    ERR_POST_TOP_OR_HOT_EXIST_TIME_INVALID(21011, "置顶/热帖子有效时间不能超过%s天"),


    ERR_FILE_IS_OVER_SIZE(23001,"文件超过大小限制"),
    ERR_UPLOAD_SWIFT_FAIL(23002,"上传对象存储失败"),
    ERR_FILE_TRANS_FAIL(23003,"文件转码失败")
    ;

    private int code;

    private String msg;

    BaseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
