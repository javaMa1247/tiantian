package com.ttsx.foos;

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
public class FoodApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodApplication.class,args);
    }
}
