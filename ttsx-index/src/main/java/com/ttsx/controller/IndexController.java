package com.ttsx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-04-29 下午 8:12
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/")  //  -->  http://localhost:6666/  -->  http://localhost:6666/index.html
    public String GoToIndex(){
        System.out.println("访问首页地址");
        return "redirect:/index.html";
    }
}
