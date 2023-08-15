package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WorkController {

    //@LoginRequired
    @RequestMapping(path = "/nofinish",method = RequestMethod.GET)
    public String nofinishJob(){
        return "site/nofinish";
    }
}
