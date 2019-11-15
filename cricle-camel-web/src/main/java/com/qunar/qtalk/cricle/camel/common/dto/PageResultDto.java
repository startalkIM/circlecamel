package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoling.wang on 2019/2/28.
 */
@Data
public class PageResultDto<T> implements Serializable {

    private Integer total;

    private List<T> rows;

    private Integer pageCount;

    public PageResultDto(Integer total, List<T> rows, Integer pageCount) {
        this.total = total;
        this.rows = rows;
        this.pageCount = pageCount;
    }

}
