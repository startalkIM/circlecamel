package com.qunar.qtalk.cricle.camel.common.handler;

import com.qunar.qtalk.cricle.camel.common.consts.MsgStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by haoling.wang on 2019/1/16.
 * <p>
 * {@link MsgStatusEnum} 类型处理器
 */
public class MsgStatusTypeHandler extends BaseTypeHandler<MsgStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, MsgStatusEnum msgStatusEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, msgStatusEnum.getCode());
    }

    @Override
    public MsgStatusEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        int code = resultSet.getInt(s);
        return MsgStatusEnum.codeOf(code);
    }

    @Override
    public MsgStatusEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int code = resultSet.getInt(i);
        return MsgStatusEnum.codeOf(code);
    }

    @Override
    public MsgStatusEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int code = callableStatement.getInt(i);
        return MsgStatusEnum.codeOf(code);
    }
}
