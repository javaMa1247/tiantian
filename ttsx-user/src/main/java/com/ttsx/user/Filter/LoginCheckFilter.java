package com.ttsx.user.Filter;

import com.alibaba.fastjson.JSON;
import com.ttsx.user.util.JWTUtils;
import com.ttsx.utils.BaseContext;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: mqb
 * @Date: 2023/3/19
 * @Time: 22:54
 * @Description:
 */
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    @Autowired
    private RedisTemplate redisTemplate;
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI=request.getRequestURI();  //backend/index.html

        log.info("拦截到请求:{}",requestURI);

        //不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/common/**",
                "/user/sendMsg", //移动端发送短信
                "/user/login" , //移动端登录
                "/user/logon",  //注册
                "/user/findPass" ,   //忘记密码
                "/user/logout",
                "/user/showName",
                "/user/checkUname",
                "/user/admin/**",//管理员
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断登录状态，如果已经登录，则直接放行
        if(request.getHeader("Authorization") != null){

            String token = request.getHeader("Authorization");
            log.info("待检测的token为:"+ token );
            System.out.println(token);
            boolean flag=this.redisTemplate.hasKey(token);
            String mno ="";
            if(  flag) {
                String t= (String) this.redisTemplate.opsForHash().get(token,"token");
                Map<String,Object> info=JWTUtils.getTokenInfo(   t   );
                mno = info.get("userid").toString();
            }else{
                log.info("token已过期");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"token已过期\"}");
                response.getWriter().flush();
                return;
            }

            log.info("用户已经登录,用户id为:{}",mno);
            Long empId = Long.parseLong(mno);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\": \"用户未登录\"}");
        response.getWriter().flush();
        return;
    }

    @Override
    public void destroy() {

    }

    //判断本次请求是否要放行
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


}
