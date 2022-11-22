package com.coder.community;


import com.coder.community.controller.HomeController;
import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.LoginTicketMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.LoginTicket;
import com.coder.community.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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
    public void testUserMapper(){
        System.out.println(userMapper.selectByEmail("2397882391@qq.com"));
        userMapper.deleteUserByName("去你奶奶的");
        System.out.println(userMapper.selectById(150));
    }

    @Test
    public void test02  (){
        User user=userMapper.selectById(21);
        System.out.println(user);
    }
    @Test
    public void test04  (){
        User user=userMapper.selectByEmail("1477191128@qq.com");
        System.out.println(user);
        user=userMapper.selectById(150);
        System.out.println(user);
        userMapper.deleteUserById(150);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
}
