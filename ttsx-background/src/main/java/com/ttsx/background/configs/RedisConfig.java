package com.ttsx.background.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //因为redis中存的value中的数据类型比较复杂，
    //它是一个对象，在redis保存时要序列化,所以提供序列化器
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate();

        //设置 序列化器
        redisTemplate.setKeySerializer(   new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(  new StringRedisSerializer() );
        //将redis中保存的hash的value ，因为它是一个对象，
        //这里调用  GenericJackson2JsonRedisSerializer序列化器将对象转为了json字符串
        redisTemplate.setValueSerializer(    new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(   new GenericJackson2JsonRedisSerializer()  );

        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

}
