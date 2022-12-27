package com.coder.community.dao;

import com.coder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPost(int userId,int offset ,int limit);
    List<DiscussPost> selectDiscussPost(int userId);
    /**@Param 用于给参数取别名
    如果这个方法只有一个参数，并且在<if>里使用
    */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    int updateCommentCount(int id, int commentCount);

    DiscussPost selectDiscussPostById(int id);
}
