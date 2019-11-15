package com.qunar.qtalk.cricle.camel.common.event;

/**
 * 事件类型
 */
public enum EventType {

    POST(0, "post"), //帖子
    COMMENT(1, "comment"), //评论
    LIKE(2, "like"),//点赞
    ATREMINDPOST(3, "postAt"),//发帖的@提醒醒
    ATREMINDCOMMENT(4, "commentAt"), //评论的@提醒
    MYREPLY(5,"myReply") // 我的回复作为一种通知类型，方便客户端处理，暂不落库
    ;
    private Integer type;
    private String name;

    EventType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static EventType of(Integer type) {
        for (EventType fileType : EventType.values()) {
            if (fileType.getType().equals(type)) {
                return fileType;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
