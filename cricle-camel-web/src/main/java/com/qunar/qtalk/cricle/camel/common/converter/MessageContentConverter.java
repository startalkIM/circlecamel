package com.qunar.qtalk.cricle.camel.common.converter;

import com.qunar.qtalk.cricle.camel.common.event.EventModel;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerConverter;
import org.springframework.stereotype.Component;

/**
 * Created by haoling.wang on 2019/1/4.
 */
@Component
public class MessageContentConverter extends DozerConverter<EventModel, String> {

    public MessageContentConverter() {
        super(EventModel.class, String.class);
    }

    @Override
    public String convertTo(EventModel eventModel, String s) {
        String msg =  eventModel.getContent();
        return msg!=null ? msg : StringUtils.EMPTY;
    }

    @Override
    public EventModel convertFrom(String s, EventModel eventModel) {

        return null;
    }
}
