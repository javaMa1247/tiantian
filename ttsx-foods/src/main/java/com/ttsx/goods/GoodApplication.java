package com.ttsx.goods;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 7:36
 */
@EnableDiscoveryClient
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.ttsx.feignApi"})
public class GoodApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodApplication.class,args);
    }
}
