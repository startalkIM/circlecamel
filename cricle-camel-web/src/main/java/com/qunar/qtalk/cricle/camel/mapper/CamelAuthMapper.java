package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.entity.CamelUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CamelAuthMapper
 *
 * @author binz.zhang
 * @date 2019/1/14
 */
@Mapper
public interface CamelAuthMapper {

   CamelUserModel selectUserModel(@Param("userId") String userId, @Param("hostId") Integer hostId);

   List<CamelUserModel> selectLegalUser();

   List<CamelUserModel> getUserByUserName(@Param("userName") String userName);
}
