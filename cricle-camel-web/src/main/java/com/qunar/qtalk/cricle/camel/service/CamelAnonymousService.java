package com.qunar.qtalk.cricle.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.datasource.DynamicDataSource;
import com.qunar.qtalk.cricle.camel.common.datasource.TargetDataSource;
import com.qunar.qtalk.cricle.camel.common.dto.AnonymouModelV2;
import com.qunar.qtalk.cricle.camel.common.vo.AnonymouseReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelAnonymous;
import com.qunar.qtalk.cricle.camel.mapper.CamelAnonymousMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelCommentMapper;
import com.qunar.qtalk.cricle.camel.mapper.CamelPostMapper;
import com.qunar.qtalk.cricle.camel.web.controller.AnonymouseController;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by haoling.wang on 2019/1/2.
 */
@Slf4j
@Service
public class CamelAnonymousService {

    public static final String SPARATE_SGIN = "|";
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelAnonymousService.class);

    @Resource
    private CamelAnonymousMapper camelAnonymousMapper;

    @Resource
    private CamelPostMapper camelPostMapper;

    @Resource
    private CamelCommentMapper camelCommentMapper;

    @TargetDataSource(value = DynamicDataSource.SLAVE)
    public int countAnonymous() {
        return camelAnonymousMapper.countAnonymous();
    }

    @TargetDataSource(value = DynamicDataSource.SLAVE)
    public List<CamelAnonymous> getAll() {
        return camelAnonymousMapper.getAll();
    }

    /**
     * 生成匿名名称
     *
     * @param userName
     * @param postId
     * @return
     */
    public CamelAnonymous generateAnonymousName(String userName, String postId) {
        List<CamelAnonymous> camelAnonymousList = getAll();
        if (CollectionUtils.isEmpty(camelAnonymousList)) {
            return null;
        }
        try {
            int index = Math.abs((userName + SPARATE_SGIN + postId).hashCode() % camelAnonymousList.size());
            log.info("generateAnonymousName the index is:{}", index);
            return camelAnonymousList.get(index);
        } catch (Exception e) {
            log.error("generateAnonymousName occur ex,userName:{},postId:{}", userName, postId, e);
        }
        return null;
    }
    public CamelAnonymous validateAnyonmous(AnonymouseReqVo anonymouseReqVo) {
        String postUUID = anonymouseReqVo.getPostId();
        String userId = anonymouseReqVo.getUser();
        String anyonmous = camelPostMapper.getAnonymousByPostIdAndUserID(postUUID, userId);
        CamelAnonymous camelAnonymous;
        if (!Strings.isNullOrEmpty(anyonmous)) {
            camelAnonymous = camelAnonymousMapper.getCamelAnonymousByAnyonmous(anyonmous);
            return camelAnonymous;
        }
        List<String> anyonmousComment = camelCommentMapper.getAnonymousByPostIdAndUserID(postUUID, userId);
        if (anyonmousComment != null ) {
            for (String a : anyonmousComment) {
                if(!Strings.isNullOrEmpty(a.trim())){
                    camelAnonymous = camelAnonymousMapper.getCamelAnonymousByAnyonmous(a);
                    return camelAnonymous;
                }
            }
        }
        return null;
    }

    public void uploadAnonymous() throws IOException {
        AnonymouModelV2 anonymouModelV2 = genQtalkConfig("anonymou.json");
        anonymouModelV2.getAnonymous().forEach(x -> {
            CamelAnonymous camelAnonymous = new CamelAnonymous();
            camelAnonymous.setAnonymous(x.getName());
            camelAnonymous.setAnonymousPhoto(x.getUrl());
            camelAnonymousMapper.insertAnonymousPhoto(camelAnonymous);
        });
    }

    private AnonymouModelV2 genQtalkConfig(String configName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(configName);
        InputStream read = classPathResource.getInputStream();
        String config = new String(ByteStreams.toByteArray(read));
        ObjectMapper mapper = new ObjectMapper();
        AnonymouModelV2 params = mapper.readValue(config, AnonymouModelV2.class);
        return params;
    }
}
