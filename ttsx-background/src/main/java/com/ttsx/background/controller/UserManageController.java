package com.ttsx.background.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.background.mapper.AdminMapper;
import com.ttsx.background.mapper.GoodsMapper;
import com.ttsx.background.mapper.OrderDao;
import com.ttsx.background.mapper.UserMapper;
import com.ttsx.background.util.JWTUtils;
import com.ttsx.background.util.SelectVariables;
import com.ttsx.bean.Memberinfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: tiantian
 * @description: 用户详情
 * @author: dx
 * @create: 2023/5/19 19:44
 */
@RestController
@Slf4j
@RequestMapping("/backgroud/user")
public class UserManageController {
    @Autowired
    private UserMapper userMapper;

//    //用户中心  用户数据
//    @RequestMapping("/selectUserInfo")
//    public Map selectUserInfo(@RequestHeader(value = "Authorization",required = false) String token) {
//        Map map = new HashMap();
//        try {
//            if (token==null){
//                map.put("code", 0);
//                map.put("msg", "用户未登录");
//            }
//            String userid = (String) JWTUtils.getTokenInfo(token).get("userid");
//            Memberinfo memberinfo = this.userMapper.selectById(userid);
//            map.put("code", 1);
//            map.put("data", memberinfo);
//            return map;
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.put("code", 0);
//            map.put("msg", e.getMessage());
//            return map;
//        }
//    }

    //管理员界面  ：  用户详情
    @RequestMapping("/showUserInfo")
    public Map showUserInfo() {
        Map map = new HashMap();
        List<Memberinfo> memberinfos = this.userMapper.selectList(new QueryWrapper<>());
        if (memberinfos.size() == 0) {
            map.put("code", 0);
            map.put("msg", "取不到用户数据");
            return map;
        }
        for (Memberinfo m : memberinfos) {
            m.setPwd(null);
        }
        map.put("code", 1);
        map.put("data", memberinfos);
        return map;
    }

    //修改用户信息
    @RequestMapping("/modify")
    public Map modify(@RequestParam(value = "mno") String mno,
                      @RequestParam("nickName") String nickName,
                      @RequestParam("email") String email) {
        Map map = new HashMap();
        try {
            if (SelectVariables.selectObjectNull(mno) || SelectVariables.selectStringNull(nickName)
                    || SelectVariables.selectStringNull(email)) {
                throw new RuntimeException("错误:请填写所有数据!");
            }
            String regex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:\\w(?:[\\w-]*\\w)?\\.)+\\w(?:[\\w-]*\\w)?";
            if (!email.matches(regex)) {
                throw new RuntimeException("邮箱格式错误!");
            }
            QueryWrapper<Memberinfo> qw = new QueryWrapper<>();
            qw.eq("mno", mno);
            Memberinfo memberinfo = new Memberinfo();
            memberinfo.setNickName(nickName);
            memberinfo.setEmail(email);
            int i = this.userMapper.update(memberinfo, qw);
            if (i == 0) {
                throw new RuntimeException("修改数据失败:请修改数据!");
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

    //封禁
    @RequestMapping("/banUser")
    public Map banUser(@RequestParam(value = "mno") String mno) {
        Map map = new HashMap();
        try {
            if (mno == null) {
                throw new RuntimeException("错误:mno为空!");
            }
            QueryWrapper<Memberinfo> qw = new QueryWrapper<>();
            qw.eq("mno", mno);
            qw.eq("status", 1).or().eq("status", 0);
            Memberinfo memberinfo = new Memberinfo();
            memberinfo.setStatus(2);
            int i = this.userMapper.update(memberinfo, qw);
            if (i == 0) {
                throw new RuntimeException("修改数据失败:修改数据为0");
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

    //解封
    @RequestMapping("/unBan")
    public Map unBan(@RequestParam(value = "mno") String mno) {
        Map map = new HashMap();
        try {
            if (mno == null) {
                throw new RuntimeException("错误:mno为空!");
            }
            QueryWrapper<Memberinfo> qw = new QueryWrapper<>();
            qw.eq("mno", mno);
            qw.eq("status", 2).or().eq("status", 0);
            Memberinfo memberinfo = new Memberinfo();
            memberinfo.setStatus(1);
            int i = this.userMapper.update(memberinfo, qw);
            if (i == 0) {
                throw new RuntimeException("修改数据失败:修改数据为0");
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

}
