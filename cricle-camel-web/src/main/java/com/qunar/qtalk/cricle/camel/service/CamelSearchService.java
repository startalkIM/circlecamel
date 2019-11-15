package com.qunar.qtalk.cricle.camel.service;

import com.qunar.qtalk.cricle.camel.common.BaseCode;
import com.qunar.qtalk.cricle.camel.common.result.JsonResult;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import com.qunar.qtalk.cricle.camel.common.util.JsonResultUtils;
import com.qunar.qtalk.cricle.camel.entity.CameSearchReturnModel;
import com.qunar.qtalk.cricle.camel.entity.CamelSearchType;
import com.qunar.qtalk.cricle.camel.mapper.CamelSearchMapper;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 搜索的接口
 */

@Service
@Slf4j
public class CamelSearchService {

    @Resource
    private CamelSearchMapper camelSearchMapper;

    private static Integer KEY_LENGTH_MIN = 1;
    private static Integer searchType = 1; //

    private boolean checkSearcCondition(CamelSearchType camelSearchType) {
        return camelSearchType != null && !Strings.isNullOrEmpty(camelSearchType.getKey()) && camelSearchType.getKey().length() > KEY_LENGTH_MIN;
    }

    /**
     * 按位操作
     *
     * @param search
     * @param position
     * @return
     */
    private boolean getSearchType(Integer search, Integer position) {
        return ((search >> position) & 0x01) == 1;
    }


    public JsonResult search(CamelSearchType camelSearchType) {
        if (!checkSearcCondition(camelSearchType)) {
            log.info("search fail due to key is invalid search is {}", JacksonUtils.obj2String(camelSearchType));
            return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), "无效搜索");
        }
        String sd = camelSearchType.getKey().replaceAll("\\\\","\\\\\\\\\\\\\\\\");
        camelSearchType.setKey(sd);
        if(camelSearchType.getKey().equalsIgnoreCase("value") || camelSearchType.getKey().equalsIgnoreCase("obj")||
                camelSearchType.getKey().equalsIgnoreCase("u003d")){
            return JsonResultUtils.success("");
        }
        boolean searchPost = getSearchType(camelSearchType.getSearchType(), 1);
        boolean searchComment = getSearchType(camelSearchType.getSearchType(), 0);

        if (searchComment && searchComment) {
            return getSeachPostAndComments(camelSearchType);
        }
        if (searchComment && !searchPost) {
            return getSerarchCommnet(camelSearchType);
        }
        if (!searchComment && searchPost) {
            return getSeachPost(camelSearchType);
        }
        return JsonResultUtils.fail(BaseCode.BADREQUEST.getCode(), "不支持的搜索类型");

    }


    public JsonResult getSerarchCommnet(CamelSearchType camelSearchType) {
        return JsonResultUtils.success();
    }


    public JsonResult getSeachPost(CamelSearchType camelSearchType) {
        return JsonResultUtils.success();
    }

    public JsonResult getSeachPostAndComments(CamelSearchType camelSearchType) {
        List<CameSearchReturnModel> returns = camelSearchMapper.searchPostAndComment(camelSearchType);
        return JsonResultUtils.success(returns);
    }
    public static void main(String[] arg)  {
        String s ="sdf\\a\\aa";
//把s中的反斜杠\ 替换为\\
        System.out.println(s);
        System.out.println(s.replaceAll("\\\\", "\\\\\\\\"));
        System.out.println(s.replace("\\", "\\\\"));
    }

}
