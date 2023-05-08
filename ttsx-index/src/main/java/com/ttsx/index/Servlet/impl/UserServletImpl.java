package com.ttsx.index.Servlet.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Memberinfo;
import com.ttsx.index.Servlet.UserServlet;
import com.ttsx.index.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 9:03
 */
@Service
public class UserServletImpl extends ServiceImpl<UserMapper, Memberinfo> implements UserServlet {

}
