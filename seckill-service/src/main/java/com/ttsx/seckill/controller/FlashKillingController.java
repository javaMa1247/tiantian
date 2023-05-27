package com.ttsx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.seckill.mapper.FlashKillingMapper;
import com.ttsx.user.util.JWTUtils;
import com.ttsx.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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
    private FeignAppFlashKilling feignAppFlashKilling;
    @Autowired
    private RedisTemplate redisTemplate;
    //展示秒杀商品信息
    @GetMapping("/showmsGoodsInfo")
    public R<List<FlashKillingVO>> selectmsGoodsInfo(@RequestParam(value = "time")  Object time ) {
        //获取当天秒杀商品集合
        List<FlashKillingVO> list = redisTemplate.opsForList().range((String) time, 0, -1);
        if (list==null){
            R.error("无法获取商品数据");
        }
        return R.success(list);
    }

    //展示秒杀商品详情
    @GetMapping("/showmsGoodsDetail")
    public R<FlashKillingVO> showmsGoodsDetail(@RequestParam(value = "time",required = false)  String time,
                                               @RequestParam("seckillId")  String fno){

        //获取秒杀商品详情
        QueryWrapper<FlashKilling> qw1 = new QueryWrapper<>();
        qw1.eq("fno",Integer.parseInt(fno));
        FlashKilling flashKilling = this.flashKillingMapper.selectOne(qw1);
        Goodsinfo goodsinfo = this.feignApp.findById(   flashKilling.getGno() ).getData();
        FlashKillingVO vo = new FlashKillingVO();
        BeanUtils.copyProperties(goodsinfo,vo);
        BeanUtils.copyProperties(flashKilling,vo);
        return R.success(vo);
    }
    //下单操作
    @PostMapping("/doSeckill")
    public R doSeckill(@RequestParam("time")  String time,
                                       @RequestParam("seckillId")  String fno,
                                       HttpServletRequest request){
        //TODO : 用户名id，商品名id

        Date now = new Date();
        FlashKillingVO flashKillingVO = this.feignAppFlashKilling.showmsGoodsDetail(time, fno).getData();
        if (flashKillingVO == null){
            return R.error("访问不到服务器");
        }
        //判断时间
        if(now.getTime() < flashKillingVO.getStart_data().getTime()){
            return R.error("非法操作");
        }

        String token = request.getHeader("Authorization");
        Object userid = JWTUtils.getTokenInfo(token).get("userid");

        return R.success("进入抢购队列,请等待结果");
    }

}
