package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.entity.CameSearchReturnModel;
import com.qunar.qtalk.cricle.camel.entity.CamelSearchType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CamelSearchMapper {

    /**
     * 只搜索帖子
     *
     * @param camelSearchType
     * @return
     */
    List<CameSearchReturnModel> searchPost(CamelSearchType camelSearchType);


    /**
     * 只搜索评论
     *
     * @param camelSearchType
     * @return
     */
    List<CameSearchReturnModel> searchComment(CamelSearchType camelSearchType);


    /**
     * 帖子跟评论混搜索
     *
     * @param camelSearchType
     * @return
     */
    List<CameSearchReturnModel> searchPostAndComment(@Param("CamelSearchType") CamelSearchType camelSearchType);


}
