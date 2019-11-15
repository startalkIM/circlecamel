package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.entity.CamelLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

@Mapper
public interface CamelLikeMapper {

    CamelLike getUserLikeSign(@Param("userId") String userId,
                              @Param("postId") String postId,
                              @Param("commentId") String commentId);

    int updateLikeInfo(@Param("userId") String userId,
                       @Param("postId") String postId,
                       @Param("commentId") String commentId,
                       @Param("likeType") Integer likeType, @Param("updateTime") Timestamp updateTime);

    int insert(CamelLike record);

    int insertSelective(CamelLike record);

    CamelLike selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CamelLike record);

    int updateByPrimaryKey(CamelLike record);
}