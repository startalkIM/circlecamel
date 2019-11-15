package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by haoling.wang on 2019/1/17.
 */
@Data
public class MessageQueryDto implements Serializable {

    private String user;

    private String host;

    private String messageId;

    private Timestamp time;

}
