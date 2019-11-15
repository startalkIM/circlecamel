package com.qunar.qtalk.cricle.camel.common.dto;

import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoling.wang on 2019/3/6.
 * <p>
 * v2版本，帖子返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamelPostResultDto implements Serializable {

    List<CamelPostAttachCommentDto> newPost;

    List<CamelDelete> deletePost;
}
