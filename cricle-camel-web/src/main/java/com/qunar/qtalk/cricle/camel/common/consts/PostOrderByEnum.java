package com.qunar.qtalk.cricle.camel.common.consts;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 后台帖子搜索排序方式枚举
 */
@Slf4j
public enum PostOrderByEnum {

    CREATE_TIME(1, "创建时间"),
    COMMENT_NUM(2, "评论量"),
    LIKE_NUM(3, "点赞量")
    ;

    public int type;

    public String desc;


    public static Table<PostOrderByEnum,PostOrderEnum,PostDtoOrderCompartorEnum> COMPARATOR_TABLE = HashBasedTable.create();


    static {

        COMPARATOR_TABLE.put(CREATE_TIME, PostOrderEnum.ASC, PostDtoOrderCompartorEnum.CREATE_TIME_ASC);
        COMPARATOR_TABLE.put(CREATE_TIME, PostOrderEnum.DESC, PostDtoOrderCompartorEnum.CREATE_TIME_DESC);

        COMPARATOR_TABLE.put(COMMENT_NUM, PostOrderEnum.ASC, PostDtoOrderCompartorEnum.COMMENT_NUM_ASC);
        COMPARATOR_TABLE.put(COMMENT_NUM, PostOrderEnum.DESC, PostDtoOrderCompartorEnum.COMMENT_NUM_DESC);

        COMPARATOR_TABLE.put(LIKE_NUM, PostOrderEnum.ASC, PostDtoOrderCompartorEnum.LIKE_NUM_ASC);
        COMPARATOR_TABLE.put(LIKE_NUM, PostOrderEnum.DESC, PostDtoOrderCompartorEnum.LIKE_NUM_DESC);
    }

    public static PostOrderByEnum typeOf(int type) {
        return EnumSet.allOf(PostOrderByEnum.class)
                .stream().filter(orderEnum -> Objects.equals(type, orderEnum.type))
                .findFirst().orElse(null);
    }

    public static PostDtoOrderCompartorEnum getCompartor(PostOrderByEnum orderByEnum,PostOrderEnum orderEnum) {
        if (COMPARATOR_TABLE.contains(orderByEnum, orderEnum)) {
            return COMPARATOR_TABLE.get(orderByEnum, orderEnum);
        }
        log.error("current don't support order {}", orderEnum);
        return null;
    }

    PostOrderByEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
