package com.ttsx.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-06 下午 9:58
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/")
    public String GoToIndex(){
        System.out.println("访问首页地址");
        return "redirect:/index.html";
    }
}
