package com.ttsx.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 7:36
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.ttsx.goods.dao")
public class GoodApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodApplication.class,args);
    }
}
