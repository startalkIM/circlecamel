package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * CamelStatisicDto
 * 用户行为数据统计Model
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
@Data
public class CamelStatisicDto {
    private Timestamp dataTime;
    private Integer activeNum=0 ;
    private Integer validActiveNum=0;
    private String topCommentPostuuid; //最高回复贴的uuid
    private Integer topCommentPost=0 ; //最高回复贴的回复数量
    private String topLikePostuuid;    //最高点赞贴的uuid
    private Integer topLikePost=0 ;     //最高点赞贴的点赞数
    private Integer postRealnameNum=0 ;  //实名发帖量
    private Integer postTotalNum=0 ; //总的发帖量
    private Integer postAnonymousNum=0;//匿名发帖量
    private Integer commentNum = 0;//总的评论数
    private Integer commentRealnameNum=0 ;//实名发帖数量
    private Integer commentAnonymousNum=0 ;//匿名发帖数量
    private Integer likeNum=0;//总的点赞数
    private String browsTimeUser;
}
