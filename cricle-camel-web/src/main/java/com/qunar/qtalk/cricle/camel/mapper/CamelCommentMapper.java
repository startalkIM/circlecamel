package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.dto.CommentQueryDto;
import com.qunar.qtalk.cricle.camel.common.vo.PageQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface CamelCommentMapper {

    int insertSelective(CamelComment record);

    List<CamelComment> selectHotComments(@Param("num") Integer num,
                                         @Param("postUUID") String postUUID,
                                         @Param("threshold") Integer threshold);

    List<CamelComment> selectWithoutTargetComment(@Param("num") Integer num,
                                                  @Param("postUUID") String postUUID,
                                                  @Param("commentUUIDList") List<String> commentUUIDList);


    List<CamelComment> selectCommentHistory(@Param("curentId") Integer currentId,
                                            @Param("num") Integer num,
                                            @Param("postUUID") String postUUID);

    CamelComment selectByCommentUUID(@Param("commentUUID") String commentUUID);

    List<CamelComment> selectByCommentUUIDList(@Param("commentUUIDList") List<String> commentUUIDList);

    CamelComment selectByPrimaryKey(Integer id);

    Integer selectMaxIdByUUID(@Param("postUUID") String postUUID);

    Integer getUsefulCommentMaxId();

    List<CamelComment> getUsefulCommentList(@Param("pageQueryVo") PageQueryVo pageQueryVo);

    int updateByPrimaryKeySelective(CamelComment record);

    Integer deleteCommentByUUID(@Param("commentUUID") String uuid);

    List<CamelComment> selectExistChild(@Param("superParent") String uuid);

    void updateCommentStatus(@Param("commentUUID") String commentUUID,
                             @Param("status") Integer status);

    int updateLikeNumByUUID(@Param("commentUUID") String commentUUID,
                            @Param("likeNum") Integer likeNum);

    Integer selectDeleteFlag(@Param("commentUUID") String commentUUID);

    List<CamelComment> getCommentListByTime(@Param("curTime") Date time);


    List<String> getAnonymousByPostIdAndUserID(@Param("postUUID") String postUUID, @Param("userID") String userID);


    List<CamelComment> selectAllComments();

    void updateParentsUUID(@Param("comments") List<CamelComment> comments);

    CamelComment selectCommentByParentUUID(@Param("parentUUID") String uuid);

    List<CamelComment> selectChildComments(@Param("superParentUUID") String uuid, @Param("postUUID") String postUUID);

    List<CamelComment> selectCommentHistoryV2(@Param("curentId") Integer currentId,
                                              @Param("num") Integer num,
                                              @Param("postUUID") String postUUID);

    List<CamelComment> selectCommentHistoryExcludeHotCommentV2(@Param("curentId") Integer currentId,
                                                               @Param("num") Integer num,
                                                               @Param("postUUID") String postUUID,
                                                               @Param("list") List<String> uuids);

    Integer getCommentLikeNum(@Param("commentUUID") String uuid);

    List<CamelComment> selectCommentListByPostUUID(@Param("postUUID") String postUUID);

    List<CamelComment> selectShowCommentListByPostUUID(@Param("postUUID") String postUUID);

    List<String> selectSuperParentCommentUser(@Param("postUUID") String postUUID);

    List<String> selectUserBySuperParentUUID(@Param("postUUID") String postUUID, @Param("superParentUUID") String superParentUUID);

    List<CamelComment> selectCommentListByQueryDto(@Param("commentQueryDto")CommentQueryDto commentQueryDto);

    List<CamelComment> selectCommentListByCreatetime(@Param("commentQueryDto") CommentQueryDto commentQueryDto,
                                                     @Param("id") Integer id, @Param("createTime") Timestamp createTime);
}