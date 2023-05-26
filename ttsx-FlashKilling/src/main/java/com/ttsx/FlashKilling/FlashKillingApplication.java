package com.ttsx.FlashKilling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.ttsx.FlashKilling.mapper")
@EnableFeignClients(basePackages = {"com.ttsx.feignApi"})
public class FlashKillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashKillingApplication.class, args);
    }

}
