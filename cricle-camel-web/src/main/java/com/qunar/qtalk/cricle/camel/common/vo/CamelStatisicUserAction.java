package com.qunar.qtalk.cricle.camel.common.vo;


import lombok.Data;

@Data
public class CamelStatisicUserAction {
    private String date;
    private Integer active=0;
    private Integer postNum=0;
    private Integer commentNum=0;
    private Integer likeNum=0;
    private Integer realNamePost=0;
    private Integer anonymousPost=0;
    private Integer realNameComment=0;
    private Integer anonymouComment=0;
    private String topCommentPostUUID="";
    private String topLikePostUUID="";
}
