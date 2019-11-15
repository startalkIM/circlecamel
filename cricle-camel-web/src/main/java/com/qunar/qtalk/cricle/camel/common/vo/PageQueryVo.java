package com.qunar.qtalk.cricle.camel.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/1/10.
 * <p>
 * 大数据量查询
 */
@Data
public class PageQueryVo implements Serializable {

    private int startIndex;

    private int endIndex;

    private int pageSize;

    private int maxIndex;

    private int curPage;

    private int totalPage;

    public PageQueryVo() {
    }

    public PageQueryVo(int pageSize, int maxIndex) {
        this.pageSize = pageSize;
        this.maxIndex = maxIndex;
        this.totalPage = maxIndex % pageSize == 0 ? maxIndex / pageSize : maxIndex / pageSize + 1;
    }

    public int getStartIndex() {
        if (curPage == 1) {
            return 1;
        }
        return (curPage - 1) * pageSize + 1;
    }

    public int getEndIndex() {
        return curPage * pageSize;
    }

    /**
     * 普通分页
     * 获取offset
     */
    public int getOffSet() {
        return (curPage - 1) * pageSize;
    }
}
