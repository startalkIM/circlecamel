package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CamelAuthInfo implements Serializable {

    private Boolean authSign;
}
