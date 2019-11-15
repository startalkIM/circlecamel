package com.qunar.qtalk.cricle.camel.service.filter.post;

import com.qunar.qtalk.cricle.camel.common.dto.CamelPostAttachCommentDto;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostResultDto;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.PostStatusUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haoling.wang on 2019/3/6.
 */
@Component
public class HotPostFilter extends BasePostFilter<PostReqVo, CamelPostResultDto> {

    public CamelPostService camelPostService;

    public DozerUtils dozerUtils;

    public HotPostFilter(CamelPostService camelPostService, DozerUtils dozerUtils) {
        this.camelPostService = camelPostService;
        this.dozerUtils = dozerUtils;
    }

    @Override
    protected boolean filter(PostReqVo postReqVo) {
        if (PostStatusUtils.needHot(postReqVo.getPostType())
                && StringUtils.isAllEmpty(postReqVo.getOwner(), postReqVo.getOwnerHost())) {
            return true;
        }
        return false;
    }

    @Override
    protected void getPostList(PostReqVo postReqVo, CamelPostResultDto camelPostResultDto) {
        List<CamelPostAttachCommentDto> newPost = camelPostResultDto.getNewPost();
        List<CamelPostDto> hotPost;
        if (CollectionUtils.isNotEmpty(hotPost = camelPostService.getHotPost())) {
            List<String> remainPostUUIDList
                    = newPost.stream().map(CamelPost::getUuid).collect(Collectors.toList());
            List<CamelPostDto> newHotPostList = hotPost.stream()
                    .filter(camelPostDto -> !remainPostUUIDList.contains(camelPostDto.getUuid()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(newHotPostList)) {
                List<CamelPostAttachCommentDto> camelPostAttachCommentDtos = dozerUtils.mapCollection(newHotPostList, CamelPostAttachCommentDto.class);
                newPost.addAll(camelPostAttachCommentDtos);
            }
        }
    }
}
