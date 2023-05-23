package com.ttsx.order.controller;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.ttsx.bean.Cartinfo;
import com.ttsx.order.biz.CartBiz;
import com.ttsx.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:14
 */
@RestController
@RequestMapping("cart")
@Slf4j
public class CartController {
    @Autowired
    private CartBiz biz;
    @RequestMapping("showAllcartInfo")
    public Map<String,Object> showAllcartInfo(@RequestHeader String uid){

        log.info(uid);
        List<Cartinfo> cartinfos = this.biz.showAllCart(uid);
        Map<String,Object> map = new HashMap<>();
        map.put("code",1);
        Map map1 = new HashMap<>();
        map1.put("cart",cartinfos);
        map1.put("count",cartinfos.size());
        map.put("data",map1);
        return map;
    }

    @RequestMapping("addCart")
    public Map<String,Object> addCart(@RequestHeader String uid,HttpServletRequest request, HttpServletResponse response) {
        //取出gno和num
        String gno = request.getParameter("gno");
        String num = request.getParameter("num");
        Map<String,Object> map = new HashMap<>();
        int i = this.biz.addCart(gno, num,uid);
        if(i!=0) {
            map.put("code", 1);
            map.put("data", "添加成功");
        }
        return map;

    }
    @RequestMapping("delgoods")
    public Map<String,Object> delgoods(@RequestHeader String uid,HttpServletRequest request, HttpServletResponse response){
        String cno=request.getParameter("cno");
        String gno=request.getParameter("gno");

        Map<String,Object> map = new HashMap<>();
        int i = this.biz.delgoods(cno,gno,uid);
        if(i!=0) {
            map.put("code", 1);
            map.put("data", "删除成功");
        }
        return map;
    }
    @RequestMapping("cleanCart")
    public Map<String,Object> cleanCart(@RequestHeader String uid,HttpServletRequest request, HttpServletResponse response){
        String cartgoods = request.getParameter("cartgoods");
        List<Map<String, Object>> lists = (List<Map<String, Object>>) new Gson().fromJson(cartgoods, List.class);
        Map<String,Object> map = new HashMap<>();
        int i = this.biz.cleanCart(lists,uid);
        if(i!=0) {
            map.put("code", 1);
            map.put("data", "删除成功");
        }
        return map;
    }

    @RequestMapping("showOnecartInfo")
    public Map<String,Object> showOnecartInfo(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        String gno = request.getParameter("gno");
        String num = request.getParameter("num");
        List<Cartinfo> list= this.biz.showOnecartInfo(gno,num);
        if(list.size()>0) {
            map.put("code", 1);
            map.put("data", list);
        }
        return map;
    }
}
