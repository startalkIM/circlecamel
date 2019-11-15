package com.qunar.qtalk.cricle.camel.common.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class PostContentDto {

    private String content;

    private List<String> imgList = Lists.newArrayList();
}

/**
 * {"content":"啦啦啦",
 * "imgList":[
 * {"addTime":0,
 * "data":"",
 * "height":0,
 * "local":"/storage/emulated/0/HappyGame/ImagePic/1967955429.jpg",
 * "ret":true,"size":0,"width":0}]}
 */

