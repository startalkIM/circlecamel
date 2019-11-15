package com.qunar.qtalk.cricle.camel.common.converter;

import com.qunar.qtalk.cricle.camel.common.consts.LikeTypeEnum;
import org.dozer.DozerConverter;
import org.springframework.stereotype.Component;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Component
public class LikeTypeEnumConverter extends DozerConverter<Integer, LikeTypeEnum> {

    public LikeTypeEnumConverter() {
        super(Integer.class, LikeTypeEnum.class);
    }

    @Override
    public LikeTypeEnum convertTo(Integer integer, LikeTypeEnum likeTypeEnum) {
        return LikeTypeEnum.typeOf(integer);
    }

    @Override
    public Integer convertFrom(LikeTypeEnum likeTypeEnum, Integer integer) {
        return likeTypeEnum.getType();
    }
}
