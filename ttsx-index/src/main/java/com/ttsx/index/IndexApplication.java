package com.ttsx.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 7:02
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ttsx")
@EnableDiscoveryClient
public class IndexApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndexApplication.class,args);
    }
}
