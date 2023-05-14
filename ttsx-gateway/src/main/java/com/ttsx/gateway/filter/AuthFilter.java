package com.ttsx.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.ttsx.user.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-14 下午 3:22
 */
//@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
//
//    @Autowired
//    private RedisTemplate redisTemplate;

    private static final String LOGIN_URL = "http://localhost10001/login.html";
    private static final List<String> EXCLUDE_PATHS = Arrays.asList("/user/login", "/user/logon"); // 需要排除的路径列表

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String path = exchange.getRequest().getPath().toString();

//       token不存在，且请求不在排除列表中，则重定向到登录页面
        if (StringUtils.isBlank(token)&&!EXCLUDE_PATHS.contains(path.trim())) {
           log.info("token为空path:{}",path);
//           redirectToLogin(exchange);
        } else {
//             token有效，且请求不在排除列表中，则重定向到登录页面
            if(validateToken(token)!=0&&!EXCLUDE_PATHS.contains(path.trim())){
                exchange.getResponse().getHeaders().add("mno",validateToken(token)+"" );
                log.info("path:{}",path);
            }
        }
        // token验证通过或请求在排除列表中，则继续处理请求
        return chain.filter(exchange);
    }
    // 重定向到登录页面
    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set(HttpHeaders.LOCATION, LOGIN_URL);
        return response.setComplete();
    }

    // 验证token的有效性
    private Integer validateToken(String token) {
        log.info("待检测的token为:"+ token );
        System.out.println(token);
//        boolean flag=this.redisTemplate.hasKey(token);
//        Integer mno =0;
//        if(  flag) {
//            String t= (String) this.redisTemplate.opsForHash().get(token,"token");
//            Map<String,Object> info= JWTUtils.getTokenInfo(   t   );
//            mno = Integer.parseInt(info.get("userid")+"");
//        }else{
//            log.info("token已过期");
//            return 0;
//        }
//        return mno;
        return 1;
    }

    @Override
    public int getOrder() {
        // 优先级，数值越小，优先级越高
        return -100;
    }
}
