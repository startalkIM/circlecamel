package com.qunar.qtalk.cricle.camel.common.vo;


import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ReplyReqVo extends BaseReqVo {

    @NotEmpty(message = "请求'我的回复'owner不能为空")
    private String owner;

    @NotEmpty(message = "请求'我的回复'ownerHost不能为空")
    private String ownerHost;


    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getOwnerHost() {
        return ownerHost;
    }

    @Override
    public void setOwnerHost(String ownerHost) {
        this.ownerHost = ownerHost;
    }
}
