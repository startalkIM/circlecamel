package com.qunar.qtalk.cricle.camel.mapper;


import com.qunar.qtalk.cricle.camel.entity.VideoInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VideoInfoMapper {

    VideoInfo getVideoInfoByMd5(@Param("videoMd5") String videoMd5);

    int deleteByPrimaryKey(Integer id);

    int insert(VideoInfo record);

    int insertSelective(VideoInfo record);

    VideoInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VideoInfo record);

    int updateByPrimaryKey(VideoInfo record);
}