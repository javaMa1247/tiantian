package com.ttsx.goods.controller;

import com.ttsx.bean.Goodsinfo;
import com.ttsx.goods.Service.GoodsService;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 3:13
 */
@RestController
@RequestMapping("goods")
@Slf4j
public class GoodController {

    @Autowired
    private GoodsService goodsService;
    @RequestMapping("findById/{fid}")
    public R<Goodsinfo> findById(@PathVariable Integer fid){
        log.info("进来了");
        Goodsinfo good = goodsService.getById(fid);
        return R.success(good);

    }
}
