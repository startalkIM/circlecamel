package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

/**
 * PostHistoryByUserVo
 *
 * @author binz.zhang
 * @date 2019/1/18
 */
@Data
public class PostHistoryByUserVo extends PostHistoryReqVo {
    private String owner;
    private String ownerHost;
}
