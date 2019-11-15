package com.qunar.qtalk.cricle.camel.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class CamelSearchType {
    private String key;        //查询关键词
    private Integer searchType = 3; //搜索类型 按位运算，01 搜评论;10搜帖子;11 搜帖子加评论
    private Integer startNum = 0;   //起始条目数
    private Integer pageNum = 20;
    private Date searchTime = new Date(); //搜索起始时间 只搜索此次时间以后的数据

}
