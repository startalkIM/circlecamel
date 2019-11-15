package com.qunar.qtalk.cricle.camel.web.interceptor;

import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.datasource.DynamicDataSource;
import com.qunar.qtalk.cricle.camel.common.util.IDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@CustomInterceptor(order = 1, addPathPatterns = {"/**"})
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestURI = httpServletRequest.getRequestURI();

        MDC.put(ContextConsts.MDC_URI, requestURI);
        MDC.put(ContextConsts.MDC_TRACE_ID, IDUtils.getUUID());
        MDC.put(ContextConsts.MDC_START_TIME, String.valueOf(System.currentTimeMillis()));

        // remove dynamicDataSource Holder sign
        DynamicDataSource.clearDataSource();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        long cost = System.currentTimeMillis() - NumberUtils.toLong(MDC.get(ContextConsts.MDC_START_TIME));
        log.info("request uri {} cost {} ms",
                MDC.get(ContextConsts.MDC_URI), cost);
        MDC.remove(ContextConsts.MDC_URI);
        MDC.remove(ContextConsts.MDC_USER);
        MDC.remove(ContextConsts.MDC_TRACE_ID);
        MDC.remove(ContextConsts.MDC_START_TIME);
    }
}
