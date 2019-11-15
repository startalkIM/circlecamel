package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.entity.CamelEventTimeline;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CamelEventTimelineMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CamelEventTimeline record);

    int insertSelective(CamelEventTimeline record);

}