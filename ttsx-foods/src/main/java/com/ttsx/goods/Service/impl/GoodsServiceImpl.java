package com.ttsx.goods.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.goods.Service.GoodsService;
import com.ttsx.goods.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

/**
 * @Author: mqb
 * @Date: 2023/5/8
 * @Time: 19:48
 * @Description:
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goodsinfo> implements GoodsService {
}
