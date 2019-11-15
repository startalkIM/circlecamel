package com.qunar.qtalk.cricle.camel.common.handler;

import com.alibaba.fastjson.JSON;
import com.qunar.qtalk.cricle.camel.common.vo.VideoVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoVoHandler extends BaseTypeHandler<VideoVo> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, VideoVo parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public VideoVo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String res = rs.getString(columnName);
        return JSON.parseObject(res, VideoVo.class);
    }

    @Override
    public VideoVo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String res = rs.getString(columnIndex);
        return JSON.parseObject(res, VideoVo.class);
    }

    @Override
    public VideoVo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String res = cs.getString(columnIndex);
        return JSON.parseObject(res, VideoVo.class);
    }
}
