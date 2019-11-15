package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * CamelEventTimeline
 * 时间线model
 * id   时间线表 时间的自增id
 * uuid 事件操作对象的uuid 如给某个帖子的点赞 则uuid 为帖子的uuid
 * type 事件的类型 点赞、取消点赞、评论、取消评论、发帖、取消发帖
 * from 事件的操作者
 * to   事件的操作对象，张三为李四点赞、张三评论李四、
 * postOwner 帖子的主人
 * @author binz.zhang
 * @date 2019/1/9
 */
@Setter
@Getter
public class CamelEventTimeline {
    private Integer id;
    private String uuid;
    private Integer eventType;
    private Timestamp createTime;
    private String fromUser;
    private String toUser;
    private String postOwner;
}
