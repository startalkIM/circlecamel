package com.qunar.qtalk.cricle.camel.service.filter.post;

import com.google.common.collect.Lists;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostAttachCommentDto;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostResultDto;
import com.qunar.qtalk.cricle.camel.common.vo.PostReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.service.filter.ServiceFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoling.wang on 2019/3/6.
 */
public class PostFilterChain implements ServiceFilter<BasePostFilter, PostReqVo, CamelPostResultDto> {

    private List<BasePostFilter> basePostFilterList = Lists.newArrayList();

    private PostReqVo postReqVo;

    @Override
    public void addServiceFilter(BasePostFilter basePostFilter) {
        basePostFilterList.add(basePostFilter);
    }

    @Override
    public CamelPostResultDto doFilter() {
        ArrayList<CamelPostAttachCommentDto> camelPostDtoList = Lists.newArrayList();
        ArrayList<CamelDelete> camelPostDeleteList = Lists.newArrayList();
        CamelPostResultDto camelPostResultDto = new CamelPostResultDto(camelPostDtoList, camelPostDeleteList);
        for (BasePostFilter basePostFilter : basePostFilterList) {
            basePostFilter.doFilter(postReqVo, camelPostResultDto);
        }
        return camelPostResultDto;
    }


    @Override
    public void setParameter(PostReqVo postReqVo) {
        this.postReqVo = postReqVo;
    }

}
