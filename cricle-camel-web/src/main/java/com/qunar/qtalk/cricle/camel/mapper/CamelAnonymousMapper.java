package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.entity.CamelAnonymous;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CamelAnonymousMapper {

    int countAnonymous();

    List<CamelAnonymous> getAll();

    int deleteByPrimaryKey(Integer id);

    CamelAnonymous selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CamelAnonymous record);

    int updateByPrimaryKey(CamelAnonymous record);

    CamelAnonymous getCamelAnonymousByAnyonmous(@Param("anyonmous") String anyonmous);

    void insertAnonymousPhoto(CamelAnonymous record);
}