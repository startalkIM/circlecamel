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
public class TopPostFilter extends BasePostFilter<PostReqVo, CamelPostResultDto> {

    public CamelPostService camelPostService;

    public DozerUtils dozerUtils;

    public TopPostFilter(CamelPostService camelPostService, DozerUtils dozerUtils) {
        this.camelPostService = camelPostService;
        this.dozerUtils = dozerUtils;
    }

    @Override
    public boolean filter(PostReqVo postReqVo) {
        if (PostStatusUtils.needTop(postReqVo.getPostType())
                && StringUtils.isAllEmpty(postReqVo.getOwner(), postReqVo.getOwnerHost())) {
            return true;
        }
        return false;
    }

    @Override
    public void getPostList(PostReqVo postReqVo, CamelPostResultDto camelPostResultDto) {
        List<CamelPostAttachCommentDto> newPost = camelPostResultDto.getNewPost();
        List<CamelPostDto> topPost;
        if (CollectionUtils.isNotEmpty(topPost = camelPostService.getTopPost())) {
            List<String> remainPostUUIDList
                    = newPost.stream().map(CamelPost::getUuid).collect(Collectors.toList());
            List<CamelPostDto> newHotPostList = topPost.stream()
                    .filter(camelPostDto -> !remainPostUUIDList.contains(camelPostDto.getUuid()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(newHotPostList)) {
                List<CamelPostAttachCommentDto> camelPostAttachCommentDtos = dozerUtils.mapCollection(newHotPostList, CamelPostAttachCommentDto.class);
                newPost.addAll(camelPostAttachCommentDtos);
            }
        }
    }
}
