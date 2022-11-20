package com.coder.community;

import com.coder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("1477191128@qq.com","test","你好，史可律小朋友！");
    }

    @Test
    public void testHTML(){
        Context context=new Context();
        context.setVariable("username","sunday");
        String content=templateEngine.process("/mail/demo",context);

        System.out.println(content);
        mailClient.sendMail("1477191128@qq.com","HTML",content);
    }
}
