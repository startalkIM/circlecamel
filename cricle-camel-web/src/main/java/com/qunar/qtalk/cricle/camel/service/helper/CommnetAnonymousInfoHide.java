package com.qunar.qtalk.cricle.camel.service.helper;

import com.qunar.qtalk.cricle.camel.common.consts.AnonymousEnum;
import com.qunar.qtalk.cricle.camel.common.dto.CamelCommentDto;
import org.springframework.stereotype.Component;

@Component("commnetAnonymousInfoHide")
public class CommnetAnonymousInfoHide extends BaseAnonyousInfoHide<CamelCommentDto> {

    @Override
    public boolean isNeedHide(CamelCommentDto camelCommentDto) {
        if (AnonymousEnum.codeOf(camelCommentDto.getIsAnonymous()).isAnonymouse) {
            return true;
        }
        return false;
    }

    @Override
    public void hide(CamelCommentDto camelCommentDto) {
        camelCommentDto.setFromUser("");
        camelCommentDto.setFromHost("");
        camelCommentDto.setOwnerName("");
    }
}
