package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.vo.NotifyConfigQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelNotifyConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CamelNotifyConfigMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CamelNotifyConfig record);

    int insertSelective(CamelNotifyConfig record);

    CamelNotifyConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CamelNotifyConfig record);

    int updateByPrimaryKey(CamelNotifyConfig record);

    CamelNotifyConfig queryNotifyConfigByUserInfo(@Param("notifyConfigQueryVo") NotifyConfigQueryVo notifyConfigQueryVo);

    int updateByUserInfo(@Param("notifyConfigQueryVo") NotifyConfigQueryVo notifyConfigQueryVo);

}