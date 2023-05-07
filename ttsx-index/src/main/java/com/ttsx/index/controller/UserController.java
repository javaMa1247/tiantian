package com.ttsx.index.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.Memberinfo;
import com.ttsx.index.Servlet.impl.UserServletImpl;
import com.ttsx.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 8:52
 */
@RestController("/user")
public class UserController {
    @Autowired
    private UserServletImpl userServlet;
    @RequestMapping("login")
    public R<Memberinfo> login(Memberinfo memberinfo){
        LambdaQueryWrapper<Memberinfo> qw =new LambdaQueryWrapper<>();
        qw.eq(Memberinfo::getNickName,memberinfo.getNickName());
        Memberinfo one = userServlet.getOne(qw);
        return R.success(one);
    }
}
