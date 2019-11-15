package com.qunar.qtalk.cricle.camel.common.dto;


import lombok.Data;

@Data
public class CamelManageSystemSerchDto {
    private String userID; //用户ID
    private String userName; //用户名字
    private String content; //帖子内容
    private String postDate; //发帖时
}
