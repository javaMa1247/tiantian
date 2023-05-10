package com.ttsx.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.bean.Goodstype;
import com.ttsx.goods.Service.GoodsService;
import com.ttsx.goods.Service.GoodsTypeService;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private GoodsTypeService goodsTypeService;

    @Autowired
    private GoodsService goodsService;

    //根据fid查询商品信息
    @RequestMapping("findById/{fid}")
    public R<Goodsinfo> findById(@PathVariable Integer fid){
        Goodsinfo good = goodsService.getById(fid);
        return R.success(good);

    }

    //展示商品信息
    @GetMapping("showGoodsInfo")
    public R<List<Goodsinfo>> selectGoodsInfo(){
        List<Goodsinfo> list = goodsService.list();
        return R.success(list);
    }

    //展示商品类型
    @GetMapping("showGoodsType")
    public R<List<Goodstype>> selectGoodsType(){
        List<Goodstype> list = goodsTypeService.list();
        return R.success(list);
    }

    //页面搜索查询，模糊查询商品并分页
    @PostMapping("findGoods")
    public R<Page> findGoods( String pageno, String goodsname, String pagesize){
        Page<Goodsinfo> page = new Page<>(Integer.parseInt(pageno),Integer.parseInt(pagesize));
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Goodsinfo::getGname,goodsname);
        goodsService.page(page,lambdaQueryWrapper);
        return R.success(page);
    }

    //显示最新商品
    //String sql="select * from goodsinfo order by gno desc limit 0,2; ";
    @GetMapping("showNewGoods")
    public R<List<Goodsinfo>> showNewGoods(){
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.orderByDesc(Goodsinfo::getGno)
                          .last(" limit 2 ");
        List<Goodsinfo> list = goodsService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    //根据Gno查询分类下的商品信息
    @PostMapping("showGoodsTno")
    public R<List<Goodsinfo>> showGoodsByTno( String pageno, String pagesize, String tno){
        Page<Goodsinfo> page = new Page<>(Integer.parseInt(pageno),Integer.parseInt(pagesize));
        LambdaQueryWrapper<Goodsinfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Goodsinfo::getTno,tno);
        List<Goodsinfo> list = goodsService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
