package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.entity.CamelSearchType;
import com.qunar.qtalk.cricle.camel.service.CamelCommentService;
import com.qunar.qtalk.cricle.camel.service.CamelPostService;
import com.qunar.qtalk.cricle.camel.service.CamelSearchService;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * CamelCommetTest
 *
 * @author binz.zhang
 * @date 2019/1/9
 */
public class CamelCommetTest extends BaseTest {

    @Resource
    private CamelPostService camelPostService;

    @Resource
    private CamelSearchService camelSearchService;
    @Test
    public void selectTest(){
        CamelSearchType camelSearchType = new CamelSearchType();
        camelSearchType.setKey("\\u");
        camelSearchType.setPageNum(10);
        camelSearchType.setSearchTime(new Date());
        camelSearchType.setStartNum(0);
        camelSearchType.setSearchType(3);
        JsonResult AS = camelSearchService.search(camelSearchType);
        System.out.println("ok");
    }
}
