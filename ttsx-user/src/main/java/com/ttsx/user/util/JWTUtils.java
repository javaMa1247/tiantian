package com.ttsx.user.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yc119_cloud_res_parent
 * @description: Token工具类
 * @author: zy
 * @create: 2023-04-30 10:00
 */
public class JWTUtils {

    private final static String SING="YC";
    private final static String User = "User";
    private final static String Admin = "Admin";
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("username","a");
        map.put("pwd","1");
//        Map<String, Object> tokenInfo = getTokenInfo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwd2QiOiIxIiwiZXhwIjoxNjgyOTk2MzIwLCJ1c2VybmFtZSI6ImEifQ.D_2_25a7hricFjlsX-Kpv42AhYWjoL9rNDd6lrPkfIU");
//        System.out.println();
//        System.out.println(tokenInfo);
       ;
//        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(creatToken(map, 3600));
//        Map<String, Claim> claims = verify.getClaims();
//        Claim claim = claims.get("");
        Map<String, Object> tokenInfo = getTokenInfo(creatToken(map, 3600));
        Object pwd = tokenInfo.get("pwd");
        System.out.println(pwd);
    }
    public static String creatToken(Map<String,String> payload,int expireTime){
        JWTCreator.Builder builder= JWT.create();
        Calendar instance=Calendar.getInstance();//获取日历对象
        if(expireTime <=0) {
            instance.add(Calendar.SECOND,3600);//默认一小时
        } else {
            instance.add(Calendar.SECOND,expireTime);
        }
        //为了方便只放入了一种类型
        payload.forEach(builder::withClaim);

        return builder
                .withExpiresAt(instance.getTime())
                .withClaim("purview",User)
                .sign(Algorithm.HMAC256(SING));
    }
    public static String creatToken(Map<String,String> payload,int expireTime,String purview){
        JWTCreator.Builder builder= JWT.create();
        Calendar instance=Calendar.getInstance();//获取日历对象
        if(expireTime <=0) {
            instance.add(Calendar.SECOND,3600);//默认一小时
        } else {
            instance.add(Calendar.SECOND,expireTime);
        }
        //为了方便只放入了一种类型
        payload.forEach(builder::withClaim);

        return builder
                .withExpiresAt(instance.getTime())
                .withClaim("purview",purview)
                .sign(Algorithm.HMAC256(SING));
    }

    public static Map<String, Object> getTokenInfo(String token){
        if( token==null||"".equals(token)){
            return null;
        }
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
        Map<String, Claim> claims = verify.getClaims();
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String expired= dateTime.format(verify.getExpiresAt());
        Map<String,Object> m=new HashMap<>();
        claims.forEach((k,v)-> m.put(k,v.asString()));
        m.put("exp",expired);
        return m;
    }
}
