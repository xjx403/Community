package com.coder.community;

import com.coder.community.entity.DiscussPost;
import com.coder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RabbitMQTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testDiscussPostService(){
        DiscussPost discussPostById = discussPostService.findDiscussPostById(291);
        System.out.println("这是查询结果" + discussPostById);
    }
}
