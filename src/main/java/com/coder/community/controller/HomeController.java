package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.DicussPostService;
import com.coder.community.service.UserService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class HomeController {
    @Autowired
    private DicussPostService dicussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化model和page,并将page注入到model里面
        //所以，在thymeleaf里可以直接访问Page对象中的数据
        page.setRows(dicussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list=dicussPostService.findDiscussPost(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> dicussPosts=new ArrayList<>();
        if(list!=null){
            for (DiscussPost post:list) {
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user=userService.findUserById(post.getUserId());
                map.put("user",user);
                dicussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",dicussPosts);
        return "/index";
    }
}
