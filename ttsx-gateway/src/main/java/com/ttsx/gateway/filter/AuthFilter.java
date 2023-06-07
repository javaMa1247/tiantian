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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String LOGIN_URL = "http://localhost:20001/login.html";
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/user/getUserId" ,
            "/goods/del",
            "/goods/addDiscuss",
            "/goods/getHistory",
            "/goods/setHistory",
            "/cart/**",
            "/order/**",
            "/addr/**"
//            "/seck/**"
            ); // 需要排除的路径列表
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String path = exchange.getRequest().getPath().toString();

//       token不存在，且请求不在排除列表中，则重定向到登录页面
        for (String url : EXCLUDE_PATHS) {
            boolean match = PATH_MATCHER.match(url, path);
            if (match) {
                if (StringUtils.isBlank(token)) {
                    log.info("token为空path:{}", path);
                    return redirectToLogin(exchange,HttpStatus.UNAUTHORIZED);
                }else {
                    try{
                        String userid = JWTUtils.getTokenInfo(token).get("userid")+"";
                        if(!"".equals(userid)) {
                            ServerHttpRequest request = exchange.getRequest();
                            ServerHttpRequest.Builder builder = request.mutate();
                            builder.header("uid", userid);
                            ServerHttpRequest newRequest = builder.build();
                            return chain.filter(exchange.mutate().request(newRequest).build());
                        }else {
                            log.info("token无效:{}", path);
                            return redirectToLogin(exchange,HttpStatus.UNAUTHORIZED);
                        }
                    }catch (Exception e){
                        log.info("token过期:{}", path);
                        return redirectToLogin(exchange,HttpStatus.FORBIDDEN);
                    }
                }

            }
        }
        // token验证通过或请求在排除列表中，则继续处理请求

        return chain.filter(exchange);
    }
    // 重定向到登录页面
    private Mono<Void> redirectToLogin(ServerWebExchange exchange,HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.LOCATION, LOGIN_URL);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        // 优先级，数值越小，优先级越高
        return -100;
    }
}
