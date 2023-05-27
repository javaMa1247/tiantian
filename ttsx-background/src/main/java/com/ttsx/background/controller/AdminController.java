package com.ttsx.background.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.*;

import com.ttsx.background.mapper.AdminMapper;
import com.ttsx.background.mapper.GoodsMapper;
import com.ttsx.background.mapper.OrderDao;
import com.ttsx.background.mapper.UserMapper;
import com.ttsx.background.util.JWTUtils;
import com.ttsx.background.util.Md5;
import com.ttsx.background.util.SelectVariables;
import com.ttsx.background.util.UploadFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/11 20:57
 */
@RestController
@Slf4j
@RequestMapping("/backgroud")
public class AdminController {
    private int expireTime = 3600;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //管理员登陆
    @RequestMapping("/adminLogin")
    public Map adminLogin(@RequestParam("aname") String aname,
                          @RequestParam("apwd") String apwd) {
        //TODO:用security做登陆的授权认证，或者其他框架
        Map map = new HashMap();
        try {
            if (aname == null || apwd == null) {
                map.put("code", 0);
                throw new RuntimeException("登录错误:管理员用户名和管理员密码存在不可取的值!");
            }
            QueryWrapper<TblAdmin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("aname", aname);
            queryWrapper.eq("apwd", (Md5.MD5Encode(apwd, "utf-8")));
            TblAdmin tblAdmin = this.adminMapper.selectOne(queryWrapper);
            if (tblAdmin == null) {
                map.put("code", 0);
                map.put("msg", "用户名或密码错误!");
                return map;
            } else {
                map.put("code", 1);
                map.put("data", tblAdmin);

                //将token存入redis
                Map m = new HashMap();
                m.put("aname", tblAdmin.getAname());
                m.put("aid", tblAdmin.getAid());
                String token = JWTUtils.creatToken(m, expireTime, "Admin");
                redisTemplate.opsForHash().put(token, "token_admin", token);
                redisTemplate.expire(token, expireTime, TimeUnit.SECONDS);

                map.put("token", token);

                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
            return map;
        }
    }

    //退出登陆
    @PostMapping("/logout")
    public Map logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            this.redisTemplate.delete(token);
        }
        Map map = new HashMap();
        map.put("code", 1);
        return map;
    }

    //修改密码
    @RequestMapping("/modifyPwd")
    public Map modifyPwd(@RequestParam("pwd1") String pwd1,
                         @RequestParam("pwd2") String pwd2,
                         @RequestParam("pwd3") String pwd3) {
        Map map = new HashMap();
        try {
            if (!pwd2.equals(pwd3)) {
                throw new RuntimeException("两次密码输入不一致!");
            }
            if (SelectVariables.selectStringNull(pwd1, pwd2, pwd3)) {
                throw new RuntimeException("密码中含有不可取的值或未输入");
            }
            pwd1 = Md5.MD5Encode(pwd1);
            pwd2 = Md5.MD5Encode(pwd2);
            Boolean flag = this.redisTemplate.hasKey("token_admin");
            if (flag) {
                String token = (String) this.redisTemplate.opsForHash().get("token", "token_admin");
                Map<String, Object> tokenInfo = JWTUtils.getTokenInfo(token);
                String aid = (String) tokenInfo.get("aid");
                QueryWrapper<TblAdmin> qw = new QueryWrapper<>();
                qw.eq("apwd", pwd1);
                qw.eq("aid", aid);
                TblAdmin tblAdmin = this.adminMapper.selectOne(qw);
                if (tblAdmin == null) {
                    throw new RuntimeException("旧密码输入有误!");
                }
                tblAdmin.setApwd(pwd2);
                String modifySql = "update tbl_admin set apwd = ? where aid = ? ";
                QueryWrapper<TblAdmin> qw2 = new QueryWrapper<>();
                qw2.eq("aid", aid);
                int i = this.adminMapper.update(tblAdmin, qw2);
                if (i == 0) {
                    throw new RuntimeException("新密码有误!");
                }
                map.put("code", 1);
                map.put("data", i);
                return map;
            } else {
                map.put("code", 0);
                map.put("msg", "请先登陆");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 1);
            map.put("msg", e.getMessage());
            return map;
        }
    }

    //检查登陆
    @RequestMapping("/selectLogin")
    public Map selectLogin(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.info("待检测的token_admin为:" + token);
        Map map = new HashMap();
        if ("".equals(token) || token == null) {
            map.put("code", 0);
            map.put("msg", "用户未登录!请先登录再进入此页面!");
            return map;
        }
        //TODO:如何取过期时间
        Boolean aBoolean = this.redisTemplate.hasKey(token);
//        String t = (String) this.redisTemplate.opsForHash().get("token", "token_admin");
//        Long ttl = this.redisTemplate.getExpire("token");
        if (aBoolean) {
            map.put("code", 1);
            Map<String, Object> info = JWTUtils.getTokenInfo(token);
            map.put("data", info);
        } else {
            map.put("code", 0);
            map.put("msg", "token过期，请重新登陆!");
        }
        return map;
    }

    @RequestMapping("/addImg")
    public Map addImg(@RequestParam("gno") String gno,
                      @RequestParam("pics") String pics) {
        Map map = new HashMap();
        try {
            Goodsinfo goodsinfo = this.goodsMapper.selectById(gno);
            if (goodsinfo == null) {
                throw new RuntimeException("查无此编号,更改图片失败...");
            }
            String sql2 = "update goodsinfo set pics = ? where gno = ? ";
            QueryWrapper<Goodsinfo> qw = new QueryWrapper<>();
            qw.eq("gno", gno);
            goodsinfo.setPics(pics);
            int i = this.goodsMapper.update(goodsinfo, qw);
            if (i == 0) {
                throw new RuntimeException("图片更改失败...更改数据为0");
            }
            map.put("code", 1);
            map.put("data", i);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping("/uploadFile")
    public Map uploadFile(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        try {
            Part uploadFile = request.getPart("uploadFile");

            String fileName = uploadFile.getSubmittedFileName();
            String filePath = "E:\\homework\\t4\\-\\ttsx-index\\src\\main\\resources\\static\\images\\goods\\" +
                    UploadFileUtils.getNowDateStr();
            String urlPath = "images/goods" + UploadFileUtils.getNowDateStr() + fileName;
            //注:线下时需服务器重启才可查看图片
            urlPath = urlPath.replaceAll("\\\\", "/");
            File saveDirFile = new File(filePath);
            if (!saveDirFile.exists()) {
                saveDirFile.mkdirs();
            }
            uploadFile.write(filePath + fileName);
            map.put("code", 1);
            map.put("data", urlPath);
            System.out.println(urlPath);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping("/test1")
    public Map test1()  {
        Map map = new HashMap();
        try{
            List<OrderIteminfoX> orderIteminfos = this.adminMapper.selectOrderiteminfo();
            map.put("code", 1);
            map.put("data", orderIteminfos);
        }catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}
