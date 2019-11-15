package com.qunar.qtalk.cricle.camel.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.dto.CamelAtListDto;
import com.qunar.qtalk.cricle.camel.common.dto.SendMessageParam;
import com.qunar.qtalk.cricle.camel.common.event.AtEventProducer;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class CamelAtService {

    public static final String SEPARATOR = "@";

    @Autowired
    private AtEventProducer atEventProducer;

    public List<SendMessageParam.ToEntity> parseAtList(String atUsers) {
        if (Strings.isNullOrEmpty(atUsers)) {
            return null;
        }
        CamelAtListDto camelAtListDto = JSONObject.parseObject(atUsers, CamelAtListDto[].class)[0];
        List<SendMessageParam.ToEntity> parseUsers = new LinkedList<>();
        List<CamelAtListDto.Data> jids = camelAtListDto.getData();
        for (CamelAtListDto.Data jid : jids) {
            SendMessageParam.ToEntity toEntity = new SendMessageParam.ToEntity();
            String[] strArr = jid.getJid().trim().split(SEPARATOR);
            if (strArr.length < 2) {
                log.error("client send the atlist param error,lack of info");
                break;
            }
            toEntity.setUser(strArr[0]);
            toEntity.setHost(strArr[1]);
            parseUsers.add(toEntity);
        }
        return parseUsers;
    }

    public List<SendMessageParam.ToEntity> getPostAtUserList(CamelPost camelPost) {
        String atList = camelPost.getAtList();
        if (Strings.isNullOrEmpty(atList)) {
            return Lists.newArrayList();
        }
        return parseAtList(atList);
    }

    public List<SendMessageParam.ToEntity> getCommentAtUserList(CamelComment camelComment) {
        String atList = camelComment.getAtList();
        if (Strings.isNullOrEmpty(atList)) {
            return Lists.newArrayList();
        }
        return parseAtList(atList);
    }

    public void actionAt(CamelPost camelPost) {
        atEventProducer.produceEvent(camelPost, getPostAtUserList(camelPost));
    }


    public List<SendMessageParam.ToEntity> actionAt(CamelComment camelComment){
        String atList = camelComment.getAtList();
        if(Strings.isNullOrEmpty(atList)){
            return null;
        }
        List<SendMessageParam.ToEntity> users = parseAtList(atList);
        atEventProducer.produceEvent(camelComment,users);
        return users;
    }

    public SendMessageParam.ToEntity parseStringToEntity(String userStr) {
        if (!Strings.isNullOrEmpty(userStr) && StringUtils.indexOf(userStr, SEPARATOR) > 0) {
            String[] strArr = StringUtils.split(userStr, SEPARATOR);
            if (strArr.length == 2) {
                SendMessageParam.ToEntity toEntity = new SendMessageParam.ToEntity();
                toEntity.setUser(strArr[0]);
                toEntity.setHost(strArr[1]);
                return toEntity;
            }
        }
        return null;
    }

    public List<SendMessageParam.ToEntity> parseStringTOEntityList(String userStr) {
        List<SendMessageParam.ToEntity> entities = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(userStr)) {
            String[] atListStrArray = StringUtils.split(userStr, ContextConsts.AT_LIST_SPLIT_SIGN);
            if (ArrayUtils.isNotEmpty(atListStrArray)) {
                Arrays.stream(atListStrArray).forEach(str->{
                    SendMessageParam.ToEntity toEntity = parseStringToEntity(str);
                    entities.add(toEntity);

                });
            }
        }
        return entities;
    }

}
