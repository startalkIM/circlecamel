package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.dto.CamelRetainModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

@Mapper
public interface CamelRetainMapper {

    CamelRetainModel selectCamelRetainByTime(@Param("time") Date time);


    void insertCamelRetainModel(@Param("camelRetainModel")CamelRetainModel camelRetainModel);


    void updateReatinRateById(@Param("retainRate")String data,@Param("id")Integer id);


    List<CamelRetainModel> selectCamelRetain(@Param("begin")Date begin, @Param("end")Date end);
}
