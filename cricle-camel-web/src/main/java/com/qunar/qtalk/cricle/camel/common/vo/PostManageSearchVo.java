package com.qunar.qtalk.cricle.camel.common.vo;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.qunar.qtalk.cricle.camel.common.consts.PostOrderByEnum;
import com.qunar.qtalk.cricle.camel.common.consts.PostOrderEnum;
import com.qunar.qtalk.cricle.camel.common.util.DateUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by haoling.wang on 2019/2/28.
 */
@Data
public class PostManageSearchVo implements Serializable {

    private String postOwner; // 发帖人ID ,多个用','分隔

    private String postOwnerName;

    private String postContent; // 帖子内容，模糊匹配

    private Integer curPage = 1;

    private Integer pageSize = 20;

    private Integer postType = 7;

    private Integer orderBy = PostOrderByEnum.CREATE_TIME.type;

    private Integer orderFlag = PostOrderEnum.DESC.type;

    /**
     * 发布开始时间
     */
    private String startDate;

    /**
     * 发布结束时间
     */
    private String endDate;

    public List<String> getPostOwerList() {
        if (Strings.isNullOrEmpty(this.getPostOwner())) {
            return null;
        }
        return Splitter.on(",").splitToList(this.getPostOwner());
    }

    public Date getStartDateWithFormat() {
        if (Strings.isNullOrEmpty(startDate)) {
            return null;
        }
        return DateUtils.parseDateWithDay(startDate.concat(" 00:00:00"), DateUtils.DEFAULT_YMDHMS_FORMAT);
    }

    public Date getEndDateWithFormat() {
        if (Strings.isNullOrEmpty(endDate)) {
            return null;
        }
        return DateUtils.parseDateWithDay(endDate.concat(" 23:59:59"), DateUtils.DEFAULT_YMDHMS_FORMAT);
    }

    public PostOrderByEnum getPostOrderEnum() {
        return PostOrderByEnum.typeOf(this.getOrderBy());
    }
}
