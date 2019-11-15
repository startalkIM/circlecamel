package com.qunar.qtalk.cricle.camel.common.consts;

import com.qunar.qtalk.cricle.camel.common.dto.CamelPostDto;

import java.util.Comparator;

/**
 * 比较器
 */
public enum PostDtoOrderCompartorEnum implements Comparator<CamelPostDto> {

    CREATE_TIME_ASC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime());
        }
    },
    CREATE_TIME_DESC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return -Long.compare(o1.getCreateTime().getTime(), o2.getCreateTime().getTime());
        }
    },

    COMMENT_NUM_ASC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return Long.compare(o1.getCommentsNum(), o2.getCommentsNum());
        }
    },
    COMMENT_NUM_DESC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return -Long.compare(o1.getCommentsNum(), o2.getCommentsNum());
        }
    },

    LIKE_NUM_ASC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return Long.compare(o1.getLikeNum(), o2.getLikeNum());
        }
    },
    LIKE_NUM_DESC() {
        @Override
        public int compare(CamelPostDto o1, CamelPostDto o2) {
            return -Long.compare(o1.getLikeNum(), o2.getLikeNum());
        }
    }
    ;


}
