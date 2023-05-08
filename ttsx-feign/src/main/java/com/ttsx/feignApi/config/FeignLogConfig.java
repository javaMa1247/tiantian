package com.ttsx.feignApi.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 黑白名单控制
 * 我们需要根据调用来源来判断该次请求是否允许放行，
 * 这时候可以使用 Sentinel 的来源访问控制（黑白名单控制）的功能。
 * 根据资源的请求来源（origin）限制资源是否通过，若配置白名单则只有请求来源位于白名单内时才可通过；
 * 若配置黑名单则请求来源位于黑名单时不通过，其余的请求通过。
 */
@Configuration
public class FeignLogConfig {
    @Bean        //NONE,BASIC,HEADERS,FULL
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 请求拦截器 统一加入 origin 请求头信息
     * feign 中的拦截器机制都是基于   责任链模式
     */
    @Component
    public class CustomerRequestInterceptor implements RequestInterceptor{
        @Override
        public void apply(RequestTemplate requestTemplate) {
            //requestTemplate  请求对象
            requestTemplate.header("source","order-source");
        }
    }
}
