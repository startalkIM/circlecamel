package com.qunar.qtalk.cricle.camel.common.vo;

import com.qunar.qtalk.cricle.camel.common.consts.ManageOpType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by haoling.wang on 2019/2/27.
 */
@Data
public class PostManageTopHotVo implements Serializable {

    @NotNull(message = "请求的帖子编号不能为空")
    private String postUUID;

    @NotNull(message = "操作的类型不能为空")
    private Integer manageOpType;

    private Integer existTime;

    public ManageOpType opType() {
        return ManageOpType.opCodeOf(this.getManageOpType());
    }
}
