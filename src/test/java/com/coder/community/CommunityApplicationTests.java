package com.coder.community;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.entity.DiscussPost;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    void contextLoads() {
        List<DiscussPost> list=discussPostMapper.selectDiscussPost(0,0,10);
        for (DiscussPost post: list) {
            System.out.println(post);
        }

        int rows=discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

}
