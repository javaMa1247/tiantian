package com.ttsx.seckill.service.imp;

import com.ttsx.seckill.config.RabbitMqConfig;
import com.ttsx.seckill.entity.Order;
import com.ttsx.seckill.scheduling.job;
import com.ttsx.seckill.service.FlashKillingService;
import com.ttsx.seckill.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:13
 */
@Service
@Slf4j
public class MqOrderServiceImpl {

    @Autowired
    private job job;
    private final OrderService orderService;
    private final FlashKillingService flashKillingService;

    public MqOrderServiceImpl(OrderService orderService, FlashKillingService flashKillingService) {
        this.orderService = orderService;
        this.flashKillingService = flashKillingService;
    }

    /**
     * MQ监听订单消息队列，并消费
     * @param order
     */
    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Message message, Order order, Channel channel) throws IOException {
        log.info("收到订单消息，订单用户为：{}，商品id为：{}", order.getOrderUser(), order.getGno());
        /**
         * 调用数据库orderService创建订单信息
         */
        order.setCreateBy(order.getOrderUser());
        order.setCreateDate(new Date());
        order.setUpdateBy(order.getOrderUser());
        order.setUpdateDate(new Date());
        order.setDelFlag("0");
        int i = orderService.saveOrder(order);
        int j = flashKillingService.decrByStock(order.getGno());
        if (i>0 && j>0){
            //消费成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            log.info("消费订单成功，订单用户为：{}，商品id为：{}", order.getOrderUser(), order.getGno());
        }else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            log.info("消费订单失败，订单用户为：{}，商品id为：{}", order.getOrderUser(), order.getGno());
        }
    }
    @RabbitListener(queues = RabbitMqConfig.STORY_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void saveStory(Message message, Channel channel)throws IOException{
        String msg = new String (message.getBody());
        log.info("接受到的消息为:{}",msg);
        job.selectjob();
        System.out.println("秒杀数据更新完成...");
    }

}
