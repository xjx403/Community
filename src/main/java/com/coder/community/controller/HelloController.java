package com.coder.community.controller;

import com.coder.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 不想想名字
 */
@Controller
@RequestMapping(path = "/test")
public class HelloController {

    @RequestMapping(path = "/hello",method = RequestMethod.GET)
    public String hello(Model model){
        String str="你好";
        model.addAttribute("content",str);
        return "site/error/demo";
    }

    //cookie演示

    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String cookieSet(HttpServletResponse response){
        //创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.generateUUD());
        //设置cookie生效范围
        cookie.setPath("/community/test");
        //设置cookie存活时间 s
        cookie.setMaxAge(60 * 10);

        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String cookieGet(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String sessionSet(HttpSession session){
        session.setAttribute("id","1");
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功！");
    }
}
