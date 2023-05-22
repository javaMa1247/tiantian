package com.ttsx.user;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 7:02
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.ttsx.user.mapper")
@ServletComponentScan
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
