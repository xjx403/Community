package com.coder.community;


import com.coder.community.controller.HomeController;
import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    UserMapper userMapper;

    void contextLoads() {
        List<DiscussPost> list=discussPostMapper.selectDiscussPost(101,0,1);
        System.out.println(list.size());
        System.out.println(list.get(0).toString());
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    public void test0  (){

    }
    @Test
    public void test01(){
        if(discussPostMapper==null) return;
        List<DiscussPost> list=discussPostMapper.selectDiscussPost(101,0,1);

        for (int i = 0; i < list.size(); i++) {
            DiscussPost post=list.get(i);
            System.out.println(post);
        }
    }

    @Test
    public void test02  (){
        User user=userMapper.selectById(1);
        System.out.println(user);
    }

    @Autowired
    private HomeController homeController;
    @Test
    public void test03 (){
        Assert.assertThat(homeController,notNullValue());
    }
}