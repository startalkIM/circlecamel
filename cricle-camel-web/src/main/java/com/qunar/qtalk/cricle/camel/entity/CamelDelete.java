package com.qunar.qtalk.cricle.camel.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * CamelDelete
 * 删除返回给客户端的数据 可适用与帖子、点赞、评论的删除返回
 *
 * @author binz.zhang
 * @date 2019/1/9
 */
@Setter
@Getter
public class CamelDelete implements Serializable {
    private String uuid;
    private Integer id;
    private Integer isDelete;

    public CamelDelete() {
    }

    public CamelDelete(String uuid, Integer id, Integer isDelete) {
        this.uuid = uuid;
        this.id = id;
        this.isDelete = isDelete;
    }
}
