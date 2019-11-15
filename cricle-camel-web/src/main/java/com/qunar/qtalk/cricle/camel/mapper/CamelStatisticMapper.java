package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

/**
 * CamelStatisticMapper
 *
 * @author binz.zhang
 * @date 2019/2/13 20:21
 */
@Mapper
public interface CamelStatisticMapper {

    int insertCamelStatisic(@Param("cst") CamelStatisicDto camelStatisicDto);

    CamelStatisicDto selectCSTByTime(@Param("curTime") Date date);

    List<CamelStatisicDto>selectCSTByTimeScorp(@Param("start") Date start,@Param("end") Date end);

}
