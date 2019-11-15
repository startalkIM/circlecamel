package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import com.qunar.qtalk.cricle.camel.entity.CamelLike;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by haoling.wang on 2019/1/8.
 */
public class CamelLikeMapperTest extends BaseTest {

    @Resource
    private CamelLikeMapper camelLikeMapper;
    @Resource
    private CamelPostService camelPostService;

//    @Test
//    public void insertSelective() {
//        CamelLike camelLike = new CamelLike();
//
//        camelLike.setLikeUuid("testLike");
//        camelLike.setLikeOwner("****");
//        camelLike.setPostUuid("******");
//        camelLike.setCommentUuid("");
//        camelLike.setCreatTime(DateUtils.getCurTimeStamp());
//
//        camelLikeMapper.insertSelective(camelLike);
//        //List<CamelDelete> deletes;
//        //deletes = camelPostService.selectDeletePostById(1,-1);
//        //System.out.print(deletes.size());
//    }
}