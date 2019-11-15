package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class CamelAtListDto {
    private Integer type;

    private List<CamelAtListDto.Data> data;

    @Getter
    @Setter
    public static class Data {
        /**
         * host : ejabhost1
         * user : lffan.liu
         */
        private String jid;
        private String text;
    }
}
