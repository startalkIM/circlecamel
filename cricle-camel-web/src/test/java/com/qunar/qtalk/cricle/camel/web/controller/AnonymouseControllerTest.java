package com.qunar.qtalk.cricle.camel.web.controller;

import com.qunar.qtalk.cricle.camel.BaseTest;
import org.junit.Test;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by haoling.wang on 2019/1/3.
 */
public class AnonymouseControllerTest extends BaseTest {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    public void getAnonymouse() {

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {

            System.out.println(entry.getKey().toString());
            System.out.println(entry.getValue().toString());
        }
    }
}