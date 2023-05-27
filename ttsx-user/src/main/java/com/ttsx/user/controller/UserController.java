package com.ttsx.user.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ttsx.bean.Memberinfo;
import com.ttsx.user.Service.UserService;
import com.ttsx.user.mapper.UserMapper;
import com.ttsx.user.util.JWTUtils;
import com.ttsx.user.util.Md5;
import com.ttsx.user.util.QQ_util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-07 下午 8:52
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    private String port;
    @Autowired
    private UserService userServlet;
    private int expireTime=3600;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @RequestMapping("login")
    public Map login(Memberinfo memberinfo, HttpServletResponse response, HttpSession session){
        LambdaQueryWrapper<Memberinfo> qw =new LambdaQueryWrapper<>();
        qw.eq(Memberinfo::getNickName,memberinfo.getNickName()).eq(Memberinfo::getPwd,(Md5.MD5Encode(memberinfo.getPwd(),"utf-8")) );
        Memberinfo one = userServlet.getOne(qw);
        Map map=new HashMap();
        if( one!=null ){
            if (one.getStatus()!=1){
                map.put("code",0);
                map.put("msg","该用户账户异常或已被封禁");
                return map;
            }
            map.put("code",1);
            Map m=new HashMap();
            m.put("nickName", one.getNickName());
            m.put("userid",one.getMno().toString());
            m.put("pwd",one.getPwd());
            String token=  JWTUtils.creatToken(   m,  expireTime  );

            session.setAttribute("token", token);

            //TODO:  对cookie里存入token
            Boolean Flag = true;
            if(Flag){//需要记住用户名
                Cookie cookie = new Cookie("token",token);
                cookie.setMaxAge(60*60*24*3);// 保存
                response.addCookie(cookie);
            }else{// 如果没有要求记住账户密码，就保存账户
                Cookie cookie = new Cookie("cookie_user", one.getNickName());
                cookie.setMaxAge(60*60*24*30);
                response.addCookie(cookie);
            }

            Map data=new HashMap<String,Object>();
            data.put("token",token);
            data.put("nickName",one.getNickName());
            map.put("data",data) ;  //   data:{   token:xxx, usernmae:xxx}
            redisTemplate.opsForHash().put(  token, "token",token);
            redisTemplate.opsForValue().set("mno",one.getMno().toString());
            log.info(redisTemplate.opsForValue().get("mno")+"");
            redisTemplate.expire(token,expireTime, TimeUnit.SECONDS);
            return map;
        }
        map.put("code",0);
        map.put("msg","查无此用户名和密码");
        return map;
    }
    @GetMapping("getUserId")
    public Integer getUserId(){
        return Integer.parseInt(redisTemplate.opsForValue().get("mno")+"");
    }

    @PostMapping("selectUserInfo")
    public Map selectUserInfo(@RequestHeader(value = "Authorization",required = false) String token) {
        Map map = new HashMap();
        try {
            if (token==null){
                map.put("code", 0);
                map.put("msg", "用户未登录");
            }
            String userid = (String) JWTUtils.getTokenInfo(token).get("userid");
            Memberinfo memberinfo = this.userMapper.selectById(userid);
            map.put("code", 1);
            map.put("data", memberinfo);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
            return map;
        }
    }
    @GetMapping("/checkLogin")
    public Map checkLogin(@RequestHeader String token){
        log.info("待检测的token为:"+ token );
        Map map = new HashMap();
        if( "".equals(token)){
            map.put("code",0);
            return map;
        }
        boolean flag=this.redisTemplate.hasKey(token);
        if(  flag) {
            map.put("code", 1);
            String t= (String) this.redisTemplate.opsForHash().get(token,"token");
            Map<String,Object> info=JWTUtils.getTokenInfo(   t   );
            map.put(   "data",info);
        }else{
            map.put("code",0);
        }
        return map;
    }

    @PostMapping("/logout")
    public Map logout(HttpServletRequest request,HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        if (token!=null){
            this.redisTemplate.delete(token);
        }
        Map map=new HashMap();
        map.put("code",1);
        return map;
    }

    @RequestMapping("/logon")
    public Map logon(@RequestParam("nickName") String nickName,
                     @RequestParam("pwd") String pwd,
                     @RequestParam("pwd1") String pwd1,
                     @RequestParam("email") String email
                     )  {
        Map map = new HashMap();
        Map map1 = new HashMap();
        Memberinfo memberinfo = new Memberinfo() ;
        try {
            //添加当前时间
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String createTime = dateFormat.format(now);//格式化然后放入字符串中
            String pwd2=(Md5.MD5Encode(pwd,"utf-8"));

            if (  nickName ==null || pwd==null || email==null){
                map.put("code",0);
                map.put("msg","不能为空");
                return map;
            }
            if (nickName.length()<5||nickName.length()>20){
                map.put("code",0);
                map.put("msg","请输入5-20个字符的用户名");
                return map;
            }
            if (!pwd.equals(pwd1)){
                map.put("code",0);
                map.put("msg","前后密码不一致");
                return map;
            }
            if (pwd.length()<8||pwd.length()>20){
                map.put("code",0);
                map.put("msg","密码最少8位，最长20位");
                return map;
            }
            if (!checkEmail(email)){
                map.put("code",0);
                map.put("msg","邮箱格式不正确");
                return map;
            }

            memberinfo.setNickName(nickName);
            memberinfo.setPwd(pwd2);
            memberinfo.setEmail(email);
            memberinfo.setRegDate(createTime);
            memberinfo.setTel("1008610086");
            memberinfo.setStatus(1);
            int i = this.userMapper.insert(memberinfo);
            if (i>0){
                map.put("code",1);
                map1.put("data",map);
                //后续成功注册操作
            }
        } catch (Exception e) {
            map.put("code",0);
            map.put("msg",e.getMessage());
            map1.put("data",map);
            e.printStackTrace();
            return map;
        }
        return map;
    }
    @RequestMapping("/deliverPort")
    public Map deliverPort(@RequestParam("nickName") String nickName,
                           @RequestParam("email") String email
                           ) {
        Map map = new HashMap();
        if(nickName == null || nickName.length() == 0){
            map.put("code",0);
            map.put("msg","未取到参数");
            return map;
        }
        try {
            LambdaQueryWrapper<Memberinfo> qw =new LambdaQueryWrapper<>();
            qw
                    .eq(Memberinfo::getNickName, nickName)
                    .eq(Memberinfo::getEmail, email);
            Memberinfo one = userServlet.getOne(qw);
            if (one==null){
                map.put("code",0);
                map.put("msg","您的邮箱与您的用户名不匹配");
                return map;
            }
            QQ_util qq=new QQ_util();
            qq.sendCode(email);
            port=qq.QQmail;
            map.put("code",1);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code",0);
            map.put("msg",e.getMessage());
            return map;
        }
        return map;
    }

    @RequestMapping("/updatePass")
    public Map updatePass(@RequestParam("port") String yzport,
                          @RequestParam("email") String email,
                          @RequestParam("pwd") String pwd,
                          @RequestParam("pwd1") String pwd1,
                          @RequestParam("nickName") String nickName
                          ) {
        Map map = new HashMap();
        try {
            if (yzport == null || "".equals(yzport)) {
                map.put("code",0);
            } else if ("".equals(email) || "".equals(yzport) || "".equals(pwd) || "".equals(pwd1)) {
                map.put("code",-3);
            } else if (!port.equals(yzport)) {
                map.put("code",-1);
            } else if (!pwd.equals(pwd1)) {
                map.put("code",-2);
            } else {
                UpdateWrapper<Memberinfo> updateWrapper = new UpdateWrapper<>();
                String pwd2=(Md5.MD5Encode(pwd,"utf-8"));
                updateWrapper.set("pwd", pwd2)
                        .eq("nickName", nickName);
                boolean update = userServlet.update(null, updateWrapper);
                if (update == true){
                    map.put("code",1);
                }
            }
        }catch (Exception e){
            map.put("code",0);
            map.put("msg",e.getMessage());
        }
        return map;
    }

    @RequestMapping("/showName")
    public Map showName(HttpServletRequest request, HttpServletResponse response)  {
        Map map1 = new HashMap();
        Map map = new HashMap();
        try{
            Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for(int i = 0;i<cookies.length;i++){
                    if(cookies[i].getName().equals("token")){
                        String pwd = JWTUtils.getTokenInfo(cookies[i].getValue()).get("pwd").toString();
                        String user = JWTUtils.getTokenInfo(cookies[i].getValue()).get("nickName").toString();
                        map.put("pwd",pwd);
                        map.put("user",user);
                    }
                }
                map1.put("code",1);
                map1.put("data",map);
            }
        }catch(Exception e){
            map1.put("code",0);
            map1.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return map1;
    }

    @RequestMapping("/checkUname")
    public Map checkUname(  @RequestParam("nickName") String nickName){
        Map map = new HashMap();
        if ("".equals(nickName)||nickName==null){
            map.put("code",-1);
            return map;
        }
        String sql = "select * from memberinfo where nickName=?";
        QueryWrapper<Memberinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickName", nickName);
        Memberinfo memberInfo = this.userMapper.selectOne(queryWrapper);
        if(memberInfo!=null){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }

    public static boolean checkEmail(String email)
    {String str = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:\\w(?:[\\w-]*\\w)?\\.)+\\w(?:[\\w-]*\\w)?";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
