package com.ttsx.goods.biz;

import com.ttsx.bean.Goodsinfo;
import com.ttsx.goods.dao.GoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 3:18
 */
@Service
public class FoodsBiz {
    @Autowired
    private GoodsDao dao;

    public Goodsinfo findById(Integer id){
        Goodsinfo goodsinfo = this.dao.selectById(id);
        return goodsinfo;
    }
}
