package com.ttsx.user.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Memberinfo;
import com.ttsx.user.Service.UserService;
import com.ttsx.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 9:03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, Memberinfo> implements UserService {
}
