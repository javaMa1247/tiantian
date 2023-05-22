package com.ttsx.background;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.ttsx.background.mapper")
@ServletComponentScan
public class BackgroundApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackgroundApplication.class,args);
    }
}
