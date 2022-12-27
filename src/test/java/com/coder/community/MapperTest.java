package com.coder.community;


import com.coder.community.controller.HomeController;
import com.coder.community.dao.*;
import com.coder.community.entity.*;
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

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private CommentMapper commentMapper;

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
        List<Comment> list = commentMapper.selectCommentsByEntity(1,228,0,Integer.MAX_VALUE);
        if(list.size() == 0){
            System.out.println("error");
        }else {
            for (Comment c: list) {
                System.out.println(c);
            }
        }

        System.out.println(commentMapper.selectCountByEntity(1,228));
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

    @Test
    public void testPostInsert1(){
        System.out.println(discussPostMapper.selectDiscussPost(151,0,10));
    }
    @Test
    public void testPostInsert() {
        // System.out.println(discussPostMapper.selectDiscussPostById(275));
        StringBuilder stringBuilder = new StringBuilder(new String().valueOf(-1234));
        StringBuilder s = stringBuilder;
        System.out.println(s);
        System.out.println(stringBuilder.reverse());
        System.out.println(stringBuilder);
        System.out.println(s.toString().equals(stringBuilder.reverse().toString()));
    }

    @Test
    public void testMessage(){
        List<Message> messages =messageMapper.selectConversations(111,0,20);
        for (Message message: messages){
            System.out.println(message);
        }

        System.out.println(messageMapper.selectConversationCount(111));

        List<Message> letters = messageMapper.selectLetters("111_112",0,20);
        for (Message message: letters){
            System.out.println(message);
        }
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);

        System.out.println(messageMapper.selectLetterUnreadCount(131,"111_131"));
    }
}
