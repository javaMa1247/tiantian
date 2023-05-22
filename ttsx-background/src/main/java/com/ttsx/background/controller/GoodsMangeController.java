package com.ttsx.background.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.background.mapper.AdminMapper;
import com.ttsx.background.mapper.GoodsMapper;
import com.ttsx.background.mapper.OrderDao;
import com.ttsx.background.mapper.UserMapper;
import com.ttsx.background.util.SelectVariables;
import com.ttsx.bean.Goodsinfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: tiantian
 * @description:  商品管理
 * @author: dx
 * @create: 2023/5/19 19:51
 */

@RestController
@Slf4j
@RequestMapping("/backgroud/goods")
public class GoodsMangeController {
    private int expireTime = 3600;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private AdminMapper adminMapper;

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

}
