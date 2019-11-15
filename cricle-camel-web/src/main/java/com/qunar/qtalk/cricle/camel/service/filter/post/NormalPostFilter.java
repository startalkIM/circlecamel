package com.qunar.qtalk.cricle.camel.service.filter.post;

import com.qunar.qtalk.cricle.camel.common.dto.CamelPostAttachCommentDto;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostResultDto;
import com.qunar.qtalk.cricle.camel.common.util.DozerUtils;
import com.qunar.qtalk.cricle.camel.common.util.PostStatusUtils;
import com.qunar.qtalk.cricle.camel.common.vo.PostReqVo;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by haoling.wang on 2019/3/6.
 */
@Component
public class NormalPostFilter extends BasePostFilter<PostReqVo, CamelPostResultDto> {

    public CamelPostService camelPostService;

    public DozerUtils dozerUtils;

    public NormalPostFilter(CamelPostService camelPostService, DozerUtils dozerUtils) {
        this.camelPostService = camelPostService;
        this.dozerUtils = dozerUtils;
    }

    @Override
    public boolean filter(PostReqVo postReqVo) {
        if (PostStatusUtils.needNormal(postReqVo.getPostType())) {
            return true;
        }
        return false;
    }

    @Override
    public void getPostList(PostReqVo postReqVo, CamelPostResultDto camelPostResultDto) {
        List<CamelPostAttachCommentDto> newPost = camelPostResultDto.getNewPost();
        List<String> remainList = newPost.stream().map(CamelPost::getUuid).collect(Collectors.toList());

        Pair<List<CamelPost>, List<CamelDelete>> postList = camelPostService.getPostList(postReqVo, false);
        List<CamelPost> left = postList.getLeft();
        List<CamelDelete> right = postList.getRight();

        List<CamelPost> collect = left.stream().filter(camelPost -> !remainList.contains(camelPost.getUuid())).collect(Collectors.toList());
        List<CamelPostDto> showCamelPostDtos = camelPostService.checkCurUserLikeInfo(collect);
        if (CollectionUtils.isNotEmpty(showCamelPostDtos)) {
            List<CamelPostAttachCommentDto> camelPostAttachCommentDtos = dozerUtils.mapCollection(showCamelPostDtos, CamelPostAttachCommentDto.class);
            newPost.addAll(camelPostAttachCommentDtos);
        }
        camelPostResultDto.getDeletePost().addAll(right);
    }
}
