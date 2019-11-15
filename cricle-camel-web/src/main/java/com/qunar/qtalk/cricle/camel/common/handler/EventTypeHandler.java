package com.qunar.qtalk.cricle.camel.common.handler;

import com.qunar.qtalk.cricle.camel.common.event.EventType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haoling.wang on 2019/1/16.
 * <p>
 * {@link com.qunar.qtalk.cricle.camel.common.event.EventType} 类型处理器
 */
public class EventTypeHandler extends BaseTypeHandler<EventType> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, EventType eventType, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, eventType.getType());
    }

    @Override
    public EventType getNullableResult(ResultSet resultSet, String s) throws SQLException {
        int code = resultSet.getInt(s);
        return EventType.of(code);
    }

    @Override
    public EventType getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int code = resultSet.getInt(i);
        return EventType.of(code);
    }

    @Override
    public EventType getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int code = callableStatement.getInt(i);
        return EventType.of(code);
    }
}
