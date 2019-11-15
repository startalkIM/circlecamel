package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.common.consts.ContextConsts;
import com.qunar.qtalk.cricle.camel.common.util.JacksonUtils;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Data
public class CamelRetainModel {

    private Integer id;  //数据id
    private Timestamp createTime; //创建时间
    private String[] browUser; //浏览人数
    private String retainRate; //留存率

    /**
     * retainRate
     * {
     * "1",70,
     * "2":59,
     * "3":40,
     * "4":30,
     * "5":10
     * ...
     * }
     */

    public void buildEmptyRetainRate() {
        HashMap<Integer, String> empty = new HashMap<>(ContextConsts.REATAIN_DATE.length);
        for (Integer i : ContextConsts.REATAIN_DATE) {
            empty.put(i, String.valueOf(0.0));
        }
        this.setRetainRate(JacksonUtils.obj2String(empty));
        return;
    }

    public static HashMap<String,String> buildEmptyRetainRateHashMap() {
        HashMap<String, String> empty = new HashMap<>(ContextConsts.REATAIN_DATE.length);
        for (Integer i : ContextConsts.REATAIN_DATE) {
            empty.put(String.valueOf(i), String.valueOf(0.0));
        }
        return empty;
    }

    public static HashMap<String,Integer> buildEmptyRetainNumHashMap() {
        HashMap<String, Integer> empty = new HashMap<>(ContextConsts.REATAIN_DATE.length);
        for (Integer i : ContextConsts.REATAIN_DATE) {
            empty.put(String.valueOf(i), 0);
        }
        return empty;
    }
}
