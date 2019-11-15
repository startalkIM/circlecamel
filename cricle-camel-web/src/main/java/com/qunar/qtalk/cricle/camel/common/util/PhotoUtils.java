package com.qunar.qtalk.cricle.camel.common.util;

import lombok.Data;

import java.util.List;

@Data
public class PhotoUtils {

    List<entiy> allPhoto;

    @Data
    public static class entiy {
        private String name;
        private String url;
    }

}
