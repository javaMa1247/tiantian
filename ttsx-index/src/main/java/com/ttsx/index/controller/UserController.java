package com.ttsx.index.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ttsx.bean.Memberinfo;
import com.ttsx.index.Service.UserService;
import com.ttsx.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 8:52
 */
@RestController("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping("login")
    public R<Memberinfo> login(Memberinfo memberinfo){
        LambdaQueryWrapper<Memberinfo> qw =new LambdaQueryWrapper<>();
        qw.eq(Memberinfo::getNickName,memberinfo.getNickName());
        Memberinfo one = userService.getOne(qw);
        return R.success(one);
    }
}
