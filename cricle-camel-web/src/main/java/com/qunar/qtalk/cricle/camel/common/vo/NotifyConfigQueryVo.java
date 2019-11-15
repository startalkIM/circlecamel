package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class NotifyConfigQueryVo implements Serializable {

    /**
     * 用户
     */
    @NotNull(message = "NotifyConfigQueryVo.notifyUser is null")
    private String notifyUser;

    /**
     * 域
     */
    @NotNull(message = "NotifyConfigQueryVo.host is null")
    private String host;


    /**
     * 通知开关，1:开 0:关
     */
    private Integer flag;

}
