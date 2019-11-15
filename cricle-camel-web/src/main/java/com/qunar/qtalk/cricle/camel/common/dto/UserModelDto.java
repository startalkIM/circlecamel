package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UserModelDto
 *
 * @author binz.zhang
 * @date 2019/1/14
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserModelDto implements Serializable {

    private String userName;
    private String userHost;

    public String getBareJid() {
        return this.userName + "@" + this.userHost;
    }
}
