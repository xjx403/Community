package com.coder.community.service;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DicussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPost(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPost(userId,offset,limit);
    }

    public  int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
