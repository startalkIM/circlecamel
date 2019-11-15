package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * CamelUserModel
 *
 * @author binz.zhang
 * @date 2019/1/18
 */
@Getter
@Setter
public class CamelUserModel {
    private String userName;
    private String userHost;
    private String userCName; // 中文名
    private String hireType;
    private String dep1;

    public CamelUserModel(String userName, String userHost) {
        this.userName = userName;
        this.userHost = userHost;
    }

    public CamelUserModel() {
    }

    @Override
    public String toString() {
        return userName+"@"+userHost;
    }

}
