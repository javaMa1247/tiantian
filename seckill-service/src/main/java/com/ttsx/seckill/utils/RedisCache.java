package com.ttsx.seckill.utils;

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
    private RedissonClient redissonClient;
    /**
     * 对指定key的键值减一
     * @param key 键
     * @return Long
     */
    public Long decrBy(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 对指定key的键值加一
     * @param key
     * @return
     */
    public Long incrBy(String key,Integer gno) {
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
        Integer v = (Integer) redisTemplate.opsForValue().get(gno +"");
        if (Objects.nonNull(v) && v > 0) {
            if (LimitNum(key, 1)) {
                return redisTemplate.opsForValue().increment(key);
            }
        }
        return 0L;
    }
    public void setCacheObject(String key, Object value, long timeout, TimeUnit timeUnit) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, timeUnit);
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
