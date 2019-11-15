package com.qunar.qtalk.cricle.camel.common.dto;

import lombok.Data;

@Data
public class CommentDeleteResultDto{
    private String commentUUID;
    private String postUUID;
    private String superParentCommentUUID;
    private Integer deleteType; //0 标识客户端此次操作是删除的主评论，1标识删除的是子评论
    private Integer superParentStatus;
    private Integer isDelete;
}
//
//    private superParentComment superParentComment;
//    private childComment childComment;
//
//
//    @Data
//    public static class superParentComment {
//        private String commentUUID;
//        private Integer isDelete;
//        private Integer commentStatus = 2; //可否直接删除，0不可，1删除了但是要继续显示，2可以直接删除不在客户端显示
//
//    }
//
//    @Data
//    public static class childComment {
//        private String commentUUID;
//        private Integer isDelete;
//    }
//}
