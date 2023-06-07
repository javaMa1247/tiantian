package com.ttsx.seckill.utils;

import com.ttsx.bean.FlashKillingVO;
import com.ttsx.seckill.controller.FlashKillingController;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:30
 */
@Component
@Slf4j
public class RedisCache {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FlashKillingController controller;

    /**
     * 对指定key的键值减一
     * @param key 键
     * @return Long
     */
    public Integer decrBy(Integer key,String time) {
        Integer fno = controller.getFnoByGno(key);
        // 从 Redis 中获取指定 gno 和 time 的 flashKillingVO 对象
        String nowTime = getNowTime.getTime();
        FlashKillingVO flashKillingVO = (FlashKillingVO)redisTemplate.opsForHash().get(nowTime + "\t" + time, String.valueOf(fno));

// 如果 flashKillingVO 对象不为 null，则对其 count 属性进行减一操作
        if (flashKillingVO != null) {
            if(flashKillingVO.getCount()>0) {
                flashKillingVO.setCount(flashKillingVO.getCount() - 1);
                // 将更新后的 flashKillingVO 对象保存回 Redis
                redisTemplate.opsForHash().put(nowTime + "\t" + time, String.valueOf(fno), flashKillingVO);
                return flashKillingVO.getCount();
            }
        }else {
            return -1;
        }
        return -1;
    }

    /**
     * 对指定key的键值加一
     * @param key
     * @return
     */
    public Long incrBy(String key,Integer gno,String time) {
//        RLock lock = redissonClient.getLock("lock:" + gno);
//        try {
//            lock.lock();
//            Integer v = (Integer) redisTemplate.opsForValue().get(gno +"");
//            if (Objects.nonNull(v) && v > 0) {
//                if (LimitNum(key, 1)) {
//                    return redisTemplate.opsForValue().increment(key);
//                }
//            }
//        } finally {
//            lock.unlock();
//        }
        String nowTime = getNowTime.getTime();
        Integer fno = controller.getFnoByGno(gno);
        // 从 Redis 中获取指定 gno 和 time 的 flashKillingVO 对象
        FlashKillingVO flashKillingVO = (FlashKillingVO) redisTemplate.opsForHash().get(nowTime + "\t" + time, String.valueOf(fno));

        if (flashKillingVO != null) {
            if (Objects.nonNull(flashKillingVO) && flashKillingVO.getCount() > 0) {
                if (LimitNum(key, 1)) {
                    return redisTemplate.opsForValue().increment(key);
                }
            }
            return 0L;
        }
        return 0L;
    }
    boolean LimitNum(String key,Integer num){
        Object o = redisTemplate.opsForValue().get(key);
        if (Objects.nonNull(o)) {
            int i = Integer.parseInt(o + "");
            if (i >= num) {
                log.info(key + "被限购");
                return false;
            }
        }
        return true;
    }
}
