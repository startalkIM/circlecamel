package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ValidateMacTokenResult {
    private boolean validate;
    private String validateMsg;
}
