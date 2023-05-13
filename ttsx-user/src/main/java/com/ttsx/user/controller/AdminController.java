package com.ttsx.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.*;

import com.ttsx.user.mapper.AdminMapper;
import com.ttsx.user.mapper.GoodsMapper;
import com.ttsx.user.mapper.OrderDao;
import com.ttsx.user.mapper.UserMapper;
import com.ttsx.user.util.JWTUtils;
import com.ttsx.user.util.Md5;
import com.ttsx.user.util.SelectVariables;
import com.ttsx.user.util.UploadFileUtils;
import com.ttsx.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
@RequestMapping("/user/admin")
public class AdminController {
    private int expireTime = 3600;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderDao orderDao;

    //用户中心  用户数据
    @RequestMapping("/selectUserInfo")
    public Map selectUserInfo(@RequestHeader(value = "Authorization",required = false) String token) {
        Map map = new HashMap();
        try {
            if (token==null){
                map.put("code", 0);
                map.put("msg", "用户未登录");
            }
            String userid = (String)JWTUtils.getTokenInfo(token).get("userid");
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

    //TODO:尚待测试
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

    /**
     * 用户详情
     */
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

    /**
     * 订单管理
     */
    //TODO:导入ttsx-order,  待解决导入问题 ：getOrderInfo，nextStatus
    //查询订单详情
    @RequestMapping("/getOrderInfo")
    public Map getOrderInfo() {
        Map map = new HashMap();
        try {
            List<OrderInfoBeanX> select = this.adminMapper.selectOrderInfo();
            for (OrderInfoBeanX orderInfoBeanX : select) {
                Integer status = orderInfoBeanX.getStatus();
                if (status == 1) {
                    orderInfoBeanX.setSstatus("已下单");
                } else if (status == 2) {
                    orderInfoBeanX.setSstatus("发货中");
                } else if (status == 3) {
                    orderInfoBeanX.setSstatus("送货中");
                } else {
                    orderInfoBeanX.setSstatus("已送达");
                }
                Integer invoice = orderInfoBeanX.getInvoice();
                if (invoice == 0) {
                    orderInfoBeanX.setSinvoice("无发票");
                } else if (invoice == 1) {
                    orderInfoBeanX.setSinvoice("电子发票");
                } else if (invoice == 2) {
                    orderInfoBeanX.setSinvoice("纸质发票");
                }
            }
            if (select.size() == 0) {
                throw new RuntimeException("无任何订单信息!");
            }
            map.put("code", 1);
            map.put("data", select);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    //下一步
    @RequestMapping("/nextStatus")
    public Map nextStatus(@RequestParam("ono") String ono) {
        Map map = new HashMap();
        try {
            Orderinfo orderinfo = this.orderDao.selectById(ono);
            Integer status = orderinfo.getStatus();
            String upSql = "update orderinfo set status = ? where ono = ? ";
            if (status != 1 && status != 2 && status != 3) {
                throw new RuntimeException("已经是最后一步!");
            }
            QueryWrapper<Orderinfo> qw1 = new QueryWrapper<>();
            qw1.eq("ono", ono);
            orderinfo.setStatus(status + 1);
            int i = this.orderDao.update(orderinfo, qw1);
            if (i == 0) {
                throw new RuntimeException("操作失败:操作数据为0");
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

    //获取订单详情
    @RequestMapping("/getOrderItem")
    public Map getOrderItem(@RequestParam("ono") String ono) {
        Map map = new HashMap();
        try {
            List<OrderShowInfoBeanX> select = this.adminMapper.selectOrderItem(ono);
            if (select.size() == 0) {
                throw new RuntimeException("订单详情遗失!请询问相关人员!");
            }
            String list = "";
            for (OrderShowInfoBeanX orderShowInfoBeanX : select) {
                list += orderShowInfoBeanX.toString();
            }
            map.put("code", 1);
            map.put("data", list);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    /**
     * 商品管理
     */

    @RequestMapping("/getGoodsInfo")
    public Map getGoodsInfo() {
        Map map = new HashMap();
        try {
            List<Goodsinfo> select = this.adminMapper.selectGoodsInfo();
            if (select.size() == 0) {
                throw new RuntimeException("查无商品数据,请联系管理员!");
            }
            map.put("code", 1);
            map.put("data", select);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping("/getSelectData")
    public Map getSelectData() {
        Map map = new HashMap();
        try {
            List<Goodsinfo> select = this.adminMapper.selectGoodsData();
            if (select.size() == 0) {
                throw new RuntimeException("查无数据,请联系管理员!");
            }
            map.put("code", 1);
            map.put("data", select);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping("/modifyGoodInfo")
    public Map modifyGoodInfo(Goodsinfo goodsInfoBeanX) {
        Map map = new HashMap();
        try {
            if (SelectVariables.selectStringNull(goodsInfoBeanX.getGname(), goodsInfoBeanX.getIntro(),
                    goodsInfoBeanX.getUnit(), goodsInfoBeanX.getQperied(), goodsInfoBeanX.getWeight()) ||
                    SelectVariables.selectObjectNull(goodsInfoBeanX.getGno(), goodsInfoBeanX.getTno(), goodsInfoBeanX.getPrice()
                            , goodsInfoBeanX.getBalance())) {
                throw new RuntimeException("请完整输入所有数据!");
            }
            Goodsinfo goodsinfo = this.goodsMapper.selectById(goodsInfoBeanX.getGno());
            if (goodsinfo == null) {
                throw new RuntimeException("查无此编号,请联系管理员.");
            }
            String moSql = "update goodsinfo set gname = ?,tno = ?,price = ?,intro = ?,balance = ?,unit = ?, " +
                    " qperied = ?,weight = ? where gno = ? ";
            goodsinfo.setGname(goodsInfoBeanX.getGname());
            goodsinfo.setTno(goodsInfoBeanX.getTno());
            goodsinfo.setPrice(goodsInfoBeanX.getPrice());
            goodsinfo.setIntro(goodsInfoBeanX.getIntro());
            goodsinfo.setBalance(goodsInfoBeanX.getBalance());
            goodsinfo.setUnit(goodsInfoBeanX.getUnit());
            goodsinfo.setQperied(goodsInfoBeanX.getQperied());
            goodsinfo.setWeight(goodsInfoBeanX.getWeight());
            goodsinfo.setGno(goodsInfoBeanX.getGno());
            int i = this.goodsMapper.updateById(goodsinfo);
            if (i == 0) {
                throw new RuntimeException("更改失败,更改数据为0,请联系管理员");
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

    @RequestMapping("/addGoodInfo")
    public Map addGoodInfo(Goodsinfo goodsInfoBeanX) {
        //GoodsInfoBeanX(gno=1, tno=1, gname=红富士, price=68.0, intro=很甜，很好吃, balance=100,
        // pics=images/goods02.jpg, unit=箱, qperied=1个月, tname=新鲜水果, weight=5KG)
        Map map = new HashMap();
        try {
            if (SelectVariables.selectStringNull(goodsInfoBeanX.getGname(), goodsInfoBeanX.getIntro(),
                    goodsInfoBeanX.getUnit(), goodsInfoBeanX.getQperied(), goodsInfoBeanX.getWeight()) ||
                    SelectVariables.selectObjectNull(goodsInfoBeanX.getTno(), goodsInfoBeanX.getPrice()
                            , goodsInfoBeanX.getBalance())) {
                throw new RuntimeException("请完整输入所有数据!");
            }
            String adSql = "insert into goodsinfo(gname,tno,price,intro,balance,unit,qperied,weight) values(?,?,?,?,?,?,?,?) ";
            Goodsinfo goodsinfo = new Goodsinfo();
            goodsinfo.setGname(goodsInfoBeanX.getGname());
            goodsinfo.setTno(goodsInfoBeanX.getTno());
            goodsinfo.setPrice(goodsInfoBeanX.getPrice());
            goodsinfo.setIntro(goodsInfoBeanX.getIntro());
            goodsinfo.setBalance(goodsInfoBeanX.getBalance());
            goodsinfo.setUnit(goodsInfoBeanX.getUnit());
            goodsinfo.setQperied(goodsInfoBeanX.getQperied());
            goodsinfo.setWeight(goodsInfoBeanX.getWeight());
            int i = this.goodsMapper.insert(goodsinfo);
            if (i == 0) {
                throw new RuntimeException("更改失败,更改数据为0,请联系管理员");
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

    @RequestMapping("/toDeletX")
    public Map toDeletX(@RequestParam("gno") String gno) {
        Map map = new HashMap();
        try {
            Goodsinfo goodsinfo = this.goodsMapper.selectById(gno);
            if (goodsinfo == null) {
                throw new RuntimeException("查无此数据!请联系管理员!");
            }
            String upSql = "delete from goodsinfo where gno = ? ";
            QueryWrapper<Goodsinfo> qw = new QueryWrapper<>();
            qw.eq("gno", gno);
            int i = this.goodsMapper.delete(qw);
            if (i == 0) {
                throw new RuntimeException("删除失败,无法删除指定数据...");
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

    @RequestMapping("/modifyGoodDescrn")
    public Map modifyGoodDescrn(Goodsinfo gi) {
        Map map = new HashMap();
        try {
            Goodsinfo goodsinfo = this.goodsMapper.selectById(gi.getGno());
            if (goodsinfo == null) {
                throw new RuntimeException("不存在该商品编号");
            }
            String sql = "update goodsinfo set descr = ? where gno = ? ";
            QueryWrapper<Goodsinfo> qw = new QueryWrapper<>();
            qw.eq("gno", gi.getGno());
            goodsinfo.setDescr(gi.getDescr());
            int i = this.goodsMapper.update(goodsinfo, qw);
            if (i == 0) {
                throw new RuntimeException("无法更改数据!请联系管理员!");
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
            String filePath = "E:\\homework\\ttsx\\-\\ttsx-index\\src\\main\\resources\\static\\images\\goods\\" +
                    UploadFileUtils.getNowDateStr();
            String urlPath = "images" + UploadFileUtils.getNowDateStr() + fileName;
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
