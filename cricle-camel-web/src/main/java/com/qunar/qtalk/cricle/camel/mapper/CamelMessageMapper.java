package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.dto.AtMessageQueryDto;
import com.qunar.qtalk.cricle.camel.common.dto.MessageQueryDto;
import com.qunar.qtalk.cricle.camel.common.event.EventType;
import com.qunar.qtalk.cricle.camel.entity.CamelMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface CamelMessageMapper {

    int insert(CamelMessage record);

    int insertSelective(CamelMessage record);

    CamelMessage selectByPrimaryKey(Integer id);

    CamelMessage getByEventTypeAndEntityId(CamelMessage camelMessage);

    List<CamelMessage> getMessageList(@Param("messageQueryDto") MessageQueryDto messageQueryDto);

    int updateByPrimaryKeySelective(CamelMessage record);

    int updateByPrimaryKey(CamelMessage record);

    Integer updateReadFlagByTime(@Param("curTime") Timestamp time, @Param("user") String user, @Param("host") String host);

    List<CamelMessage> selectMessageListByQueryDto(@Param("queryDto") AtMessageQueryDto queryDto);

    List<CamelMessage> selectMessageListByCreatetime(@Param("queryDto") AtMessageQueryDto queryDto,
                                                     @Param("id") Integer id, @Param("createTime") Timestamp createTime);

    Integer updatePostAtMessage2DeleteFlag(@Param("eventType") EventType eventType, @Param("postUUID") String postUUID,
                                           @Param("userList") List<String> userList);

    Integer updateCommentAtMessage2DeleteFlag(@Param("eventType") EventType eventType, @Param("commentUUID") String commentUUID);
}