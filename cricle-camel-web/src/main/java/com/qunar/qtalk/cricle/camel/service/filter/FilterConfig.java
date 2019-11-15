package com.qunar.qtalk.cricle.camel.service.filter;

import com.qunar.qtalk.cricle.camel.service.filter.post.HotPostFilter;
import com.qunar.qtalk.cricle.camel.service.filter.post.NormalPostFilter;
import com.qunar.qtalk.cricle.camel.service.filter.post.PostFilterChain;
import com.qunar.qtalk.cricle.camel.service.filter.post.TopPostFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haoling.wang on 2019/3/7.
 */
@Configuration
public class FilterConfig {

    @Bean
    public PostFilterChain postFilterChain(TopPostFilter topPostFilter,
                                           HotPostFilter hotPostFilter,
                                           NormalPostFilter normalPostFilter) {
        PostFilterChain postFilterChain = new PostFilterChain();
        postFilterChain.addServiceFilter(topPostFilter);
        postFilterChain.addServiceFilter(hotPostFilter);
        postFilterChain.addServiceFilter(normalPostFilter);
        return postFilterChain;
    }
}
