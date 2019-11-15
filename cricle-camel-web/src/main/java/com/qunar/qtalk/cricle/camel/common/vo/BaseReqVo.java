package com.qunar.qtalk.cricle.camel.common.vo;

import java.io.Serializable;

public abstract class BaseReqVo implements Serializable {

    protected Long createTime;

    protected Integer pageSize = 20;

    public abstract String getOwner();

    public abstract void setOwner(String owner);

    public abstract String getOwnerHost();

    public abstract void setOwnerHost(String ownerHost);

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
