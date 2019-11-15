package com.qunar.qtalk.cricle.camel.web.interceptor;

import com.qunar.qtalk.cricle.camel.common.holder.UserHolder;
import com.qunar.qtalk.cricle.camel.common.util.CookieAuthUtils;
import com.qunar.qtalk.cricle.camel.service.CamelStatisticDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * StatisticDataInterceptor
 *
 * @author binz.zhang
 * @date 2019/2/13
 */
@Slf4j
@CustomInterceptor(order = 3, addPathPatterns = {"/**"})
public class StatisticDataInterceptor implements HandlerInterceptor {

    private static final List<String> vaildActrveUser = Arrays.asList(new String[]{"/post", "/deletePost", "/uploadComment", "/deleteComment","/deleteComment/V2","/like","/post/V2",
                                                                                    "/uploadComment/V2"});

    private static final List<String> activeUser = Arrays.asList(new String[]{"/getPostList","/getPostList/v2"});

    @Resource
    private CamelStatisticDataService camelStatisticDataService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestURI = httpServletRequest.getRequestURI();
        String userName = UserHolder.getValue(CookieAuthUtils.KEY_USER_NAME);
        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        String time = simpleDateFormat.format(today);

        try {
            activeUser.stream().forEach(x->{
                if(requestURI.endsWith(x)){
                    camelStatisticDataService.recordActiveUser(userName);
                    camelStatisticDataService.recordBrowsUser(time, userName);
                }
            });
            vaildActrveUser.stream().forEach(x -> {
                if (requestURI.endsWith(x)) {
                    camelStatisticDataService.recordValidActiveUser(userName);
                }
            });


        } catch (Exception e) {
            log.error("StatisticDataInterceptor exception", e);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
