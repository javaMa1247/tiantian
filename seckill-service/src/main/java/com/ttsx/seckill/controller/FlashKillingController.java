package com.ttsx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.seckill.mapper.FlashKillingMapper;
import com.ttsx.seckill.service.imp.MqStockServiceImpl;
import com.ttsx.seckill.utils.getNowTime;
import com.ttsx.user.util.JWTUtils;
import com.ttsx.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/23 20:54
 */
@RestController
@RequestMapping("/fk")
public class FlashKillingController {
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private FlashKillingMapper flashKillingMapper;

    @Autowired
    private RedisTemplate redisTemplate;




    @RequestMapping("getFnoByGno")
    public Integer getFnoByGno( Integer gno ) {
        // 创建查询条件
        QueryWrapper<FlashKilling> qw = new QueryWrapper<>();
        qw.eq("gno", gno);

// 使用 selectOne 方法查询符合条件的单条记录
        FlashKilling flashKilling = flashKillingMapper.selectOne(qw);

// 使用 selectList 方法查询符合条件的多条记录
        List<FlashKilling> flashKillingList = flashKillingMapper.selectList(qw);
        return flashKillingList.get(0).getFno();

    }
    //展示秒杀商品信息
    @GetMapping("/showmsGoodsInfo")
    public R<List<FlashKillingVO>> selectmsGoodsInfo(@RequestParam(value = "time")  Object time ) {
        //获取当天秒杀商品集合
        String nowTime = getNowTime.getTime();
        List list = this.redisTemplate.opsForHash().values(nowTime + "\t" + time);
        if (list==null){
            R.error("无法获取商品数据");
        }
        return R.success(list);
    }

    //展示秒杀商品详情
    @GetMapping("/showmsGoodsDetail")
    public R<FlashKillingVO> showmsGoodsDetail(@RequestParam(value = "time",required = false)  String time,
                                               @RequestParam("seckillId")  String fno){
        String nowTime = getNowTime.getTime();
        FlashKillingVO vo = (FlashKillingVO) this.redisTemplate.opsForHash().get(nowTime + "\t" + time, String.valueOf(fno));
        if (vo==null){
            R.error("无法获取商品数据");
        }
        return R.success(vo);
    }


}
