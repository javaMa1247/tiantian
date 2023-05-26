package com.ttsx.FlashKilling.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.FlashKilling.Service.GoodsService;
import com.ttsx.FlashKilling.mapper.GoodsMapper;
import com.ttsx.bean.Goodsinfo;

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
