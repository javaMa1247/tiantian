package com.ttsx.seckill.scheduling;


import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.seckill.controller.FlashKillingControllerFegin;
import com.ttsx.seckill.service.FlashKillingService;
import com.ttsx.seckill.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: -
 * @description: 定时任务，查询秒杀商品，存到redis里
 * @author: dx
 * @create: 2023/5/25 20:28
 */
@Component
public class job {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private FlashKillingService flashKillingService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FlashKillingControllerFegin feignAppFlashKilling;
    //每天执行一次
    @Scheduled(cron =" 0 0 0 * * ?")
//    @Scheduled(fixedRate = 1000)
    public void selectjob()  {
        System.out.println("执行定时上架....");
        List<FlashKilling> flashKillingList = flashKillingService.selectList();
        for (FlashKilling flashKillingTtem : flashKillingList) {
            redisCache.setCacheObject(String.valueOf(flashKillingTtem.getGno()),flashKillingTtem.getCount(),3600*24, TimeUnit.SECONDS);
        }
//
        redisTemplate.delete("10");
        redisTemplate.delete("16");
        redisTemplate.delete("22");
        List<FlashKillingVO> data = feignAppFlashKilling.selectmsGoodsInfo("10").getData();
        if (!data.isEmpty()) {
            redisTemplate.opsForList().leftPushAll("10", data.toArray());
        }
        List<FlashKillingVO> data2 = feignAppFlashKilling.selectmsGoodsInfo("16").getData();
        if (!data2.isEmpty()) {
            redisTemplate.opsForList().leftPushAll("16", data2.toArray());
        }
        List<FlashKillingVO> data3 = feignAppFlashKilling.selectmsGoodsInfo("22").getData();
        if (!data3.isEmpty()) {
            redisTemplate.opsForList().leftPushAll("22", data3.toArray());
        }
        System.out.println("完成....");
    }
    @PostConstruct
    public void init() {
        selectjob();
    }

}
