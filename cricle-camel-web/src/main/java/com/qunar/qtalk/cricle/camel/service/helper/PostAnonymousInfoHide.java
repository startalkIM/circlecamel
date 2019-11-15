package com.qunar.qtalk.cricle.camel.service.helper;

import com.qunar.qtalk.cricle.camel.common.consts.AnonymousEnum;
import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;
import org.springframework.stereotype.Component;

@Component("postAnonymousInfoHide")
public class PostAnonymousInfoHide extends BaseAnonyousInfoHide<CamelPostDto> {

    @Override
    public boolean isNeedHide(CamelPostDto camelPostDto) {
        if (AnonymousEnum.codeOf(camelPostDto.getIsAnonymous()).isAnonymouse) {
            return true;
        }
        return false;
    }

    @Override
    public void hide(CamelPostDto camelPostDto) {
        camelPostDto.setOwner("");
        camelPostDto.setOwnerHost("");
        camelPostDto.setOwnerName("");
    }
}
