package com.ttsx.seckill.scheduling;


import com.ttsx.seckill.entity.FlashKilling;
import com.ttsx.seckill.service.FlashKillingService;
import com.ttsx.seckill.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    //每天执行一次
    @Scheduled(cron =" 0 0 0 * * ?")
    @Scheduled(fixedRate = 1000)
    public void selectjob()  {
        List<FlashKilling> flashKillingList = flashKillingService.selectList();
        for (FlashKilling flashKillingTtem : flashKillingList) {
            redisCache.setCacheObject(flashKillingTtem.getGno()+"",flashKillingTtem.getCount(),3600*24, TimeUnit.SECONDS);
        }
    }
    @PostConstruct
    public void init() {
        selectjob();
    }

}
