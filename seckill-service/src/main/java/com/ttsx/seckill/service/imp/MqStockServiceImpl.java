package com.ttsx.seckill.service.imp;


import com.ttsx.seckill.config.RabbitMqConfig;
import com.ttsx.seckill.entity.Order;
import com.ttsx.seckill.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private OrderServiceImpl orderService;

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
     * @param userName  用户名称
     * @param gno 商品id
     * @return String
     */
    public String secKill(String userName, Integer gno) {
        log.info("参加秒杀的用户是：{}，秒杀的商品id是：{}", userName, gno);
        String message = "";

        /**
         * 是否限购
         */
        if(!limitNumber(userName, gno)){
            message = "用户：" + userName + "商品被限购,秒杀结束";
            return message;
        }
        RLock lock = redissonClient.getLock("gno_lock");
        lock.lock();
        //redis中key对应的value减一
        try{
            // 在获取锁之后执行减少库存量的操作
            Long decrByResult = redisCache.decrBy(gno+"");
            if (Objects.nonNull(decrByResult) && decrByResult >= 0) {

                /**
                 * 说明该商品的库存量有剩余，可以进行下订单操作
                 */
                log.info("用户：{}， 秒杀该商品id：{}，库存余量{}，可以进行下订单操作", userName, gno, decrByResult);
                //1.发消息给订单消息队列，创建订单 2.发消息给库存消息队列，将库存数据减一 3.将订单保存到redis 实现限购功能

                Order order = new Order();
                order.setOrderUser(userName);
                order.setGno(gno);
                rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_EXCHANGE,RabbitMqConfig.ORDER_ROUTING_KEY,order);
                message = "用户" + userName + "秒杀商品id:" + gno + "成功";

            } else {
                /**
                 * 说明该商品的库存量没有剩余，直接返回秒杀失败的消息给用户
                 */
                log.info("用户：{}秒杀时商品商品id：{}的库存量没有剩余,秒杀结束", userName,gno);
                message = "用户：" + userName + "商品id"+gno+"的库存量没有剩余,秒杀结束";
            }

        }finally {
            lock.unlock();
        }

        return message;
    }

    private boolean limitNumber(String userName, Integer gno) {
        String key = userName + ":" + gno;
        Long incr = redisCache.incrBy(key,gno);
        return incr != 0L;
    }

}
