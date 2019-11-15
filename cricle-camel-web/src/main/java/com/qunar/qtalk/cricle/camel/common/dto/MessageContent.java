package com.qunar.qtalk.cricle.camel.common.dto;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/17.
 */
@Data
public class MessageContent implements Serializable {

    private String uuid;
    private String userFrom;
    private String userFromHost;
    private String fromIsAnonymous;
    private String fromAnonymousName;
    private String fromAnonymousPhoto;
    private String userTo;
    private String userToHost;
    private String toIsAnonymous;
    private String toAnonymousName;
    private String toAnonymousPhoto;
    private String postUUID;
    private Timestamp createTime;
    private Integer readState;
    private Integer eventType;
    private String content;


    public static void main(String[] args) {
        String str = "{\"userFrom\":\"hubo.hu\",\"readState\":0,\"postUUID\":\"a0e1563a-8caf-4044-b429-45bd438cbe23\",\"fromIsAnonymous\":0,\"toAnonymousName\":\"\",\"toIsAnonymous\":0,\"eventType\":1,\"fromAnonymousPhoto\":\"\",\"userTo\":\"hubin.hu\",\"uuid\":\"A997E2B2869A4A979510466BC6B78ACB\",\"toAnonymousPhoto\":\"\",\"content\":\"ghh\",\"userToHost\":\"ejabhost1\",\"createTime\":1547730884729,\"userFromHost\":\"ejabhost1\",\"fromAnonymousName\":\"\"}";
        //HashMap hashMap = JSON.parseObject(str, HashMap.class);
//        hashMap.forEach((k,v)->{
//            System.out.println(k);
//        });

        MessageContent messageContent = JSON.parseObject(str, MessageContent.class);
        System.out.println(messageContent.getContent());
    }
}
