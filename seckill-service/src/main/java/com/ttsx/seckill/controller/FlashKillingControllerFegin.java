package com.ttsx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.seckill.mapper.FlashKillingMapper;
import com.ttsx.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: -
 * @description:  秒杀的fegin类
 * @author: dx
 * @create: 2023/5/23 20:54
 */
@RestController
@RequestMapping("/fkFegin")
public class FlashKillingControllerFegin {
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private FlashKillingMapper flashKillingMapper;

    //展示秒杀商品信息
    @GetMapping("/showmsGoodsInfo")
    public R<List<FlashKillingVO>> selectmsGoodsInfo(@RequestParam(value = "time")  Object time ) {
        //获取当天秒杀商品集合

        QueryWrapper<FlashKilling> qw = new QueryWrapper<>();
        qw.eq("time", Integer.parseInt(time+""));
        qw.eq("start_data", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        List<FlashKilling> list = flashKillingMapper.selectList(qw);
        List<FlashKillingVO> flashKillingVOS = new ArrayList<>();
        for (FlashKilling f : list) {
            FlashKillingVO vo = new FlashKillingVO();
            Goodsinfo goodsinfo = this.feignApp.findById(f.getGno()).getData();
            System.out.println(goodsinfo);
            //将数据copy到vo对象中
            BeanUtils.copyProperties(goodsinfo,vo);
            BeanUtils.copyProperties(f,vo);
            vo.setFk_price(f.getFkPrice());
            vo.setCurrentCount(goodsinfo.getBalance());
            vo.setStart_dateString(f.getStart_data());
            flashKillingVOS.add(vo);
        }

        return R.success(flashKillingVOS);
    }
    //查询所有秒杀商品
    @GetMapping("/showmsGoodsInfoAll")
    public R<List<FlashKillingVO>> selectmsGoodsInfoAll() {
        //获取当天秒杀商品集合
        QueryWrapper<FlashKilling> qw = new QueryWrapper<>();
        List<FlashKilling> list = flashKillingMapper.selectList(qw);
        List<FlashKillingVO> flashKillingVOS = new ArrayList<>();
        for (FlashKilling f : list) {
            FlashKillingVO vo = new FlashKillingVO();
            Goodsinfo goodsinfo = this.feignApp.findById(f.getGno()).getData();
            //将数据copy到vo对象中
            BeanUtils.copyProperties(goodsinfo,vo);
            BeanUtils.copyProperties(f,vo);
            vo.setFk_price(f.getFkPrice());
            vo.setCurrentCount(goodsinfo.getBalance());
            vo.setStart_dateString(f.getStart_data());
            flashKillingVOS.add(vo);
        }

        return R.success(flashKillingVOS);
    }
    
    //展示秒杀商品详情
    @GetMapping("/showmsGoodsDetail")
    public R<FlashKillingVO> showmsGoodsDetail(@RequestParam(value = "time",required = false)  String time,
                                               @RequestParam("seckillId")  String fno){

        //获取秒杀商品详情
        QueryWrapper<FlashKilling> qw1 = new QueryWrapper<>();
        qw1.eq("fno",Integer.parseInt(fno));
        FlashKilling flashKilling = this.flashKillingMapper.selectOne(qw1);
        Goodsinfo goodsinfo = this.feignApp.findById(flashKilling.getGno()).getData();
        FlashKillingVO vo = new FlashKillingVO();
        BeanUtils.copyProperties(goodsinfo,vo);
        BeanUtils.copyProperties(flashKilling,vo);
        return R.success(vo);
    }



}
