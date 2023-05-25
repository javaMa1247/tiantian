package com.ttsx.seckill.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:15
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 库存交换机
     */

    public static final String STORY_EXCHANGE = "STORY_EXCHANGE";

    /**
     * 订单交换机
     */
    public static final String ORDER_EXCHANGE = "ORDER_EXCHANGE";

    /**
     * 库存队列
     */
    public static final String STORY_QUEUE = "STORY_QUEUE";

    /**
     * 订单队列
     */
    public static final String ORDER_QUEUE = "ORDER_QUEUE";

    /**
     * 库存路由键
     */
    public static final String STORY_ROUTING_KEY = "STORY_ROUTING_KEY";

    /**
     * 订单路由键
     */
    public static final String ORDER_ROUTING_KEY = "ORDER_ROUTING_KEY";

    /**
     *
     * @param connectionFactory
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //手动确认消息
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //消费数量
        factory.setPrefetchCount(50);
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建库存交换机
     * @return
     */
    @Bean
    public Exchange getStoryExchange() {
        return ExchangeBuilder.directExchange(STORY_EXCHANGE).durable(true).build();
    }

    /**
     * 创建库存队列
     * @return
     */
    @Bean
    public Queue getStoryQueue() {
        return new Queue(STORY_QUEUE,true);
    }

    /**
     * 库存交换机和库存队列绑定
     * @return
     */
    @Bean
    public Binding bindStory() {
        return BindingBuilder.bind(getStoryQueue()).to(getStoryExchange()).with(STORY_ROUTING_KEY).noargs();
    }

    /**
     * 创建订单队列
     * @return
     */
    @Bean
    public Queue getOrderQueue() {
        return new Queue(ORDER_QUEUE);
    }

    /**
     * 创建订单交换机
     * @return
     */
    @Bean
    public Exchange getOrderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }

    /**
     * 订单队列与订单交换机进行绑定
     * @return
     */
    @Bean
    public Binding bindOrder() {
        return BindingBuilder.bind(getOrderQueue()).to(getOrderExchange()).with(ORDER_ROUTING_KEY).noargs();
    }

}
