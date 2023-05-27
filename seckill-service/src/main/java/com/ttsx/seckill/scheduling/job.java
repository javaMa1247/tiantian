package com.ttsx.seckill.scheduling;


import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.seckill.controller.FlashKillingControllerFegin;
import com.ttsx.seckill.service.FlashKillingService;
import com.ttsx.seckill.utils.RedisCache;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
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
    private RedisTemplate redisTemplate;
    @Autowired
    private FlashKillingControllerFegin feignAppFlashKilling;
    //每天执行一次
    @Scheduled(cron =" 0 0 0 * * ?")
//    @Scheduled(fixedRate = 1000)
    public void selectjob()  {
        System.out.println("执行定时上架....");
//        List<FlashKilling> flashKillingList = flashKillingService.selectList();
//        for (FlashKilling flashKillingTtem : flashKillingList) {
//            redisCache.setCacheObject(String.valueOf(flashKillingTtem.getGno()),flashKillingTtem.getCount(),3600*24, TimeUnit.SECONDS);
//        }
//
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 昨天时间
        String yesterdayTime = sdf.format(yesterday);
        // 现在时间
        String nowTime = sdf.format(now);
        // 明天时间
        LocalDate today = LocalDate.now();
        LocalDate tomorrowTime = today.plusDays(1);

        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

       /* redisTemplate.delete(yesterdayTime+"\t"+"10");
        redisTemplate.delete(yesterdayTime+"\t"+"16");
        redisTemplate.delete(yesterdayTime+"\t"+"22");
*/
        List<FlashKillingVO> data = feignAppFlashKilling.selectmsGoodsInfo(10).getData();
        for (FlashKillingVO flashKillingVO : data) {
            redisTemplate.opsForHash().put(nowTime+"\t"+"10",String.valueOf(flashKillingVO.getFno()),flashKillingVO);
        }
        List<FlashKillingVO> data2 = feignAppFlashKilling.selectmsGoodsInfo(16).getData();
        for (FlashKillingVO flashKillingVO : data2) {
            redisTemplate.opsForHash().put(nowTime+"\t"+"16",String.valueOf(flashKillingVO.getFno()),flashKillingVO);
        }
        List<FlashKillingVO> data3 = feignAppFlashKilling.selectmsGoodsInfo(22).getData();
        for (FlashKillingVO flashKillingVO : data3) {
            redisTemplate.opsForHash().put(nowTime+"\t"+"22",String.valueOf(flashKillingVO.getFno()),flashKillingVO);
        }


        System.out.println("完成....");
    }
    @PostConstruct
    public void init() {
        selectjob();
    }

}
