package com.ttsx.goods.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Discuss;
import com.ttsx.goods.Service.DiscussService;
import com.ttsx.goods.mapper.discussMapper;
import org.springframework.stereotype.Service;

/**
 * @Author: mqb
 * @Date: 2023/5/11
 * @Time: 19:29
 * @Description:
 */
@Service
public class DiscussServiceImpl extends ServiceImpl<discussMapper, Discuss>  implements DiscussService {
}
