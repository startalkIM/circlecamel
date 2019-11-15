package com.qunar.qtalk.cricle.camel.common.converter;

import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import org.dozer.DozerConverter;
import org.springframework.stereotype.Component;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Component
public class OpTypeEnumConverter extends DozerConverter<Integer, OpTypeEnum> {

    public OpTypeEnumConverter() {
        super(Integer.class, OpTypeEnum.class);
    }

    @Override
    public OpTypeEnum convertTo(Integer type, OpTypeEnum opTypeEnum) {
        return OpTypeEnum.typeOf(type);
    }

    @Override
    public Integer convertFrom(OpTypeEnum opTypeEnum, Integer type) {
        return opTypeEnum.getType();
    }
}
