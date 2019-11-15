package com.qunar.qtalk.cricle.camel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.dto.AnonymouModelV2;
import com.qunar.qtalk.cricle.camel.common.util.HttpClientUtils;
import com.qunar.qtalk.cricle.camel.common.util.Md5CaculateUtil;
import com.qunar.qtalk.cricle.camel.entity.CamelAnonymous;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by haoling.wang on 2019/1/14.
 */
public class CamelAnonymousServiceTest extends BaseTest {

    @Resource
    private CamelAnonymousService camelAnonymousService;

    @Test
    public void generateAnonymousName() throws IOException {
        camelAnonymousService.uploadAnonymous();
    }

    @Test
    public void generateUUID() {
        for (int i = 0; i < 10; i++) {
            String uuidRandom = UUID.randomUUID().toString();
            System.out.println(uuidRandom);
        }

    }


}