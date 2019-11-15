package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.BaseTest;
import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * CamelAuthTest
 *
 * @author binz.zhang
 * @date 2019/1/14
 */
public class CamelAuthTest extends BaseTest {
    @Resource
    private CamelAuthMapper camelAuthMapper;

    @Test
    public void selectTest(){
        CamelUserModel camelUserModel = camelAuthMapper.selectUserModel("****",1);
        System.out.println(camelUserModel.getHireType());
    }

    @Test
    public void selectLegalUser() {
        List<CamelUserModel> camelUserModels = camelAuthMapper.selectLegalUser();
        System.out.println(camelUserModels.size());
    }
}
