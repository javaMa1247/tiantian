package com.ttsx.seckill;

import com.ttsx.seckill.entity.FlashKilling;
import com.ttsx.seckill.service.FlashKillingService;
import com.ttsx.seckill.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-25 下午 7:40
 */
@SpringBootApplication
public class SeckillApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }


    @Autowired
    private RedisCache redisCache;
    @Autowired
    private FlashKillingService flashKillingService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<FlashKilling> flashKillingList = flashKillingService.selectList();
        for (FlashKilling flashKillingTtem : flashKillingList) {
            redisCache.setCacheObject(flashKillingTtem.getGno()+"",flashKillingTtem.getCount(),3600, TimeUnit.SECONDS);
        }
    }

}
