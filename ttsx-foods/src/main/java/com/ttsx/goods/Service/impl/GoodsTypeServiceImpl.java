package com.ttsx.goods.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.Goodstype;
import com.ttsx.goods.Service.GoodsTypeService;
import com.ttsx.goods.mapper.GoodsMapper;
import com.ttsx.goods.mapper.GoodsTypeMapper;
import org.springframework.stereotype.Service;

/**
 * @Author: mqb
 * @Date: 2023/5/8
 * @Time: 20:14
 * @Description:
 */
@Service
public class GoodsTypeServiceImpl extends ServiceImpl<GoodsTypeMapper, Goodstype> implements GoodsTypeService {
}
