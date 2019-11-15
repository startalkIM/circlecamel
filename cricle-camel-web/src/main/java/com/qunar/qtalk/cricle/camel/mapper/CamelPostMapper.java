package com.qunar.qtalk.cricle.camel.mapper;

import com.qunar.qtalk.cricle.camel.common.dto.PostQueryDto;
import com.qunar.qtalk.cricle.camel.common.exception.MySQLException;
import com.qunar.qtalk.cricle.camel.common.vo.PageQueryVo;
import com.qunar.qtalk.cricle.camel.entity.CamelDelete;
import com.qunar.qtalk.cricle.camel.entity.CamelPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Mapper
public interface CamelPostMapper {

    List<CamelPost> selectPostHistory(@Param("curPostId") Integer curPostId,
                                      @Param("pageSize") Integer pageSize);

    Integer insertCamelPost(@Param("camelPostModel") CamelPost camelPost, @Param("searchContent") String searchContent);

    CamelPost selectByPrimaryKey(Integer id);

    CamelPost selectByPostUUID(@Param("uuid") String postUUID);

    List<CamelPost> selectByPostUUIDList(@Param("postUUIDList") List<String> postUUIDList);

    int updateByPrimaryKeySelective(CamelPost record);

    List<CamelPost> selectNewExistPost(@Param("param") Integer param) throws MySQLException;


    List<CamelDelete> selectDeletePostById(@Param("startId") Integer startId, @Param("endId") Integer endID);

    int updateDeleteFlag(@Param("uuid") String uuid,
                         @Param("deleteFlag") int deleteFlag);

    Integer selectIdByUUID(@Param("uuid") String uuid);

    Integer selectUsefulPostMaxId();

    List<CamelPost> getUsefulPostList(@Param("pageQueryVo") PageQueryVo pageQueryVo);

    Integer updatePostLikeNum(@Param("uuid") String postUUID, @Param("likeNum") Integer likeNum);

    CamelPost selectPostByUUID(@Param("uuid") String uuid);

    Integer addCommentNum(@Param("uuid") String uuid, @Param("num") Integer num);

    List<CamelPost> selectPostByUser(@Param("user") String user, @Param("host") String host,
                                     @Param("curPostId") Integer curPostId,
                                     @Param("pageSize") Integer pageSize);

    /**
     * @param postQueryDto
     * @param type         查询类型，0标识查询自己的帖子，1标识查询别人的帖子
     * @return
     */
    List<CamelPost> getPostList(@Param("postQueryDto") PostQueryDto postQueryDto,
                                @Param("selectType") boolean type);

    /**
     * @param createTime
     * @param type       查询类型，0标识查询自己的帖子，1标识查询别人的帖子
     * @return
     */
    List<CamelPost> getPostByCreateTime(@Param("createTime") Timestamp createTime,
                                        @Param("id") Integer id,
                                        @Param("postQueryDto") PostQueryDto postQueryDto,
                                        @Param("selectType") boolean type);

    List<CamelPost> getOwnPostList(@Param("postQueryDto") PostQueryDto postQueryDto);

    List<CamelPost> getOwnPostByCreateTime(@Param("createTime") Timestamp createTime,
                                           @Param("id") Integer id,
                                           @Param("postQueryDto") PostQueryDto postQueryDto);

    List<CamelPost> getPostListByTime(@Param("curTime") Date time);


    String getAnonymousByPostIdAndUserID(@Param("postUUID") String postUUID, @Param("userID") String userID);

    Integer getPostListBySearchCount(@Param("postOwnerList") List<String> postOwnerList,
                                     @Param("postContent") String postContent,
                                     @Param("excludePostList") List<String> excludePostList,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    List<CamelPost> getPostListBySearch(@Param("postOwnerList") List<String> postOwnerList,
                                        @Param("postContent") String postContent,
                                        @Param("excludePostList") List<String> excludePostList,
                                        @Param("pageQueryVo") PageQueryVo pageQueryVo,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("orderBy") int orderBy, @Param("order") int order);

    List<CamelPost> getAllUserfulPost();

    Integer getPostCommentsNum(@Param("postUUID") String postUUID);


    Integer getPostLikeNum(@Param("postUUID") String postUUID);


    List<CamelPost> selectPostByIdScorp(@Param("beginId") Integer beginId, @Param("endId") Integer endId);

    void updateSearchContent(@Param("postUUID") String postUUID, @Param("searchContent") String searchContent);
}