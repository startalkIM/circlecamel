package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

@Data
public class CamelStatisicFlowModel {
    private Integer active;
    private LinkedHashMap<String,Integer> browseUser;

}
