package com.ttsx.seckill.service.imp;

import com.ttsx.bean.FlashKillingVO;
import com.ttsx.seckill.config.RabbitMqConfig;
import com.ttsx.seckill.entity.Order;
import com.ttsx.seckill.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:17
 */
@Service
@Slf4j
public class MqStockServiceImpl {

    @Autowired
    private RedissonClient redissonClient;
    private final RedisCache redisCache;
    private final RabbitTemplate rabbitTemplate;

    public MqStockServiceImpl(RedisCache redisCache, RabbitTemplate rabbitTemplate) {
        this.redisCache = redisCache;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 使用redis+消息队列进行秒杀实现
     *
     * @param uid 用户id
     * @param gno 商品id
     * @param time 场次
     * @return String
     */
    public Map<String, Object> secKill(Integer gno, String uid, String time) {
        Map<String, Object> map = new HashMap<>();
        log.info("参加秒杀的用户id是：{}，秒杀的商品id是：{},场次：{}", uid, gno, time);
        String message = "";

        /**
         * 是否限购
         */
        if (!limitNumber(gno, uid, time)) {
            message = "用户id：" + uid + "商品" + gno + " 场次：" + time + "被限购,秒杀结束";
            map.put("code", 0);
            map.put("msg", message);
            return map;
        }
        RLock lock = redissonClient.getLock("gno_lock");
        lock.lock();
        // redis中key对应的value减一
        try {
            // 在获取锁之后执行减少库存量的操作
            Integer decrByResult = redisCache.decrBy(gno, time + "");
            if (Objects.nonNull(decrByResult) && decrByResult > 0) {

                /**
                 * 说明该商品的库存量有剩余，可以进行下订单操作
                 */
                log.info("用户：{}， 秒杀该商品id：{}，库存余量{}，可以进行下订单操作", uid, gno, decrByResult);
                // 1.发消息给订单消息队列，创建订单 2.发消息给库存消息队列，将库存数据减一 3.将订单保存到redis 实现限购功能

                Order order = new Order();
                order.setOrderUser(uid);
                order.setGno(gno);
                rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_EXCHANGE, RabbitMqConfig.ORDER_ROUTING_KEY, order);
                message = "用户id" + uid + "秒杀商品id:" + gno + "场次：" + time + "成功";
                map.put("code", 1);
                map.put("msg", message);

            } else {
                /**
                 * 说明该商品的库存量没有剩余，直接返回秒杀失败的消息给用户
                 */
                log.info("用户id：{}秒杀时商品商品id：{}的库存量没有剩余,秒杀结束", uid, gno);
                message = "用户id：" + uid + "商品id" + gno + "的库存量没有剩余,秒杀结束";
                map.put("code", 0);
                map.put("msg", message);
            }

        } finally {
            lock.unlock();
        }

        return map;
    }

    private boolean limitNumber(Integer gno, String uid, String time) {
        String key = uid + ":" + gno + ":" + time;
        Long incr = redisCache.incrBy(key, gno, time);
        return incr != 0L;
    }

}
