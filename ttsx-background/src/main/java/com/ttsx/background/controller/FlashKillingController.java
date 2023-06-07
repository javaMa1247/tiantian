package com.ttsx.background.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.background.mapper.flashKillingDao;
import com.ttsx.background.util.SelectVariables;
import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.seckill.config.RabbitMqConfig;
import com.ttsx.seckill.scheduling.job;
import com.ttsx.utils.R;
import io.micrometer.core.instrument.binder.BaseUnits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/26 10:44
 */
@RestController
@Slf4j
@RequestMapping("/backgroud/FlashKilling")
public class FlashKillingController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private FeignAppFlashKilling feignAppFlashKilling;
    @Autowired
    private com.ttsx.background.mapper.flashKillingDao flashKillingDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //查询
    @RequestMapping("/showmsGoodsInfo")
    public R<List<FlashKillingVO>> selectmsGoodsInfo(){
        List<FlashKillingVO> data = this.feignAppFlashKilling.selectmsGoodsInfoAll().getData();
        if (data == null){
            R.error("无法获取数据");
        }
        return R.success(data);
    }
    //添加秒杀商品
    @RequestMapping("/addGoodInfo")
    public Map addGoodInfo(@RequestParam("gno") String gno,
                           @RequestParam("fk_price") String fk_price,
                           @RequestParam("count") String count,
                           @RequestParam("start_data") String start_dataString,
                           @RequestParam("time") String time) throws ParseException {
        Map map = new HashMap();

        FlashKilling fk = new FlashKilling();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = formatter.parse(start_dataString);
        fk.setStart_data(start_dataString);
        fk.setGno(Integer.parseInt(gno));
        Goodsinfo goodsinfo = this.feignApp.findById(fk.getGno()).getData();
        if(Objects.nonNull(goodsinfo)){
            if(goodsinfo.getBalance() <Integer.parseInt(count)||goodsinfo.getPrice() <Integer.parseInt(fk_price)){
                map.put("code", 0);
                map.put("msg", "库存或价格错误");
                return map;
            }
        }
        fk.setFkPrice(Double.parseDouble(fk_price));
        fk.setCount(Integer.parseInt(count));
        fk.setTime(Integer.parseInt(time));
        int i = this.flashKillingDao.insert(fk);
        if (i==0){
            map.put("code",0);
            map.put("msg","无法插入数据");
            return  map;
        }
        String message = "1";
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.STORY_EXCHANGE,RabbitMqConfig.STORY_ROUTING_KEY,message.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            map.put("code",0);
            map.put("msg","无法插入数据");
            return  map;
        }
        map.put("code",1);
        return  map;
    }
    //删除
    @RequestMapping("/toDeletX")
    public Map toDeletX(@RequestParam("fno") String fno) {
        Map map = new HashMap();
        try {
            QueryWrapper<FlashKilling> qw = new QueryWrapper<>();
            qw.eq("fno", fno);
            FlashKilling flashKilling = this.flashKillingDao.selectOne(qw);
            if (flashKilling == null) {
                throw new RuntimeException("查无此数据!请联系管理员!");
            }
            String upSql = "delete from FlashKilling where gno = ? ";

            int i = this.flashKillingDao.delete(qw);
            if (i == 0) {
                throw new RuntimeException("删除失败,无法删除指定数据...");
            }
            map.put("code", 1);
            map.put("data", i);
            String message = "1";
            try {
                rabbitTemplate.convertAndSend(RabbitMqConfig.STORY_EXCHANGE,RabbitMqConfig.STORY_ROUTING_KEY,message.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                map.put("code",0);
                map.put("msg","无法插入数据");
                return  map;
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }

        return map;
    }
    //修改
    @RequestMapping("/modifyGoodInfo")
    public Map modifyGoodInfo(@RequestParam("fno") String fno,
                              @RequestParam("gno") String gno,
                              @RequestParam("fk_price") String fk_price,
                              @RequestParam("count") String count,
                              @RequestParam("currentCount") String currentCount,
                              @RequestParam("start_dateString") String start_dateString,
                              @RequestParam("time") String time) {
        Map map = new HashMap();
        try {
            QueryWrapper<FlashKilling> qw = new QueryWrapper<>();
            qw.eq("fno", fno);
            FlashKilling flag = this.flashKillingDao.selectOne(qw);
            if (flag == null) {
                throw new RuntimeException("查无此编号,请联系管理员.");
            }
            FlashKilling fk = new FlashKilling();

//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = formatter.parse(start_dateString);

            fk.setFno(Integer.parseInt(fno));
            fk.setStart_data(start_dateString);
            fk.setGno(Integer.parseInt(gno));
            Goodsinfo goodsinfo = this.feignApp.findById(fk.getGno()).getData();
            if(Objects.nonNull(goodsinfo)){
                if(goodsinfo.getBalance() <Integer.parseInt(count)||goodsinfo.getPrice() <Integer.parseInt(fk_price)){
                    map.put("code", 0);
                    map.put("msg", "库存或价格错误");
                    return map;
                }
            }
            fk.setFkPrice(Double.parseDouble(fk_price));
            fk.setCount(Integer.parseInt(count));
            fk.setTime(Integer.parseInt(time));

            int i = this.flashKillingDao.updateById(fk);
            if (i == 0) {
                throw new RuntimeException("更改失败,更改数据为0,请联系管理员");
            }
            map.put("code", 1);
            map.put("data", i);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }String message = "1";
        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.STORY_EXCHANGE,RabbitMqConfig.STORY_ROUTING_KEY,message.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            map.put("code",0);
            map.put("msg","无法插入数据");
            return  map;
        }
        return map;
    }

}
