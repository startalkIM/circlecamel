package com.qunar.qtalk.cricle.camel.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamelNotifyConfig {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 通知的key
     */
    private String notifyKey;

    /**
     * 用户
     */
    private String notifyUser;

    /**
     * 域
     */
    private String host;

    /**
     * 通知开关，1:开 0:关
     */
    private Integer flag;

    /**
     * 修改的版本，每次修改都递增
     */
    private Integer updateVersion;

    /**
     * 更新时间
     */
    private Timestamp updateTime;
}