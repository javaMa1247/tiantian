package com.ttsx.order.controller;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.ttsx.bean.Orderinfo;
import com.ttsx.order.biz.OrderBiz;
import com.ttsx.order.biz.OrderBizTmpl;
import com.ttsx.utils.PageBean;
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
 * @CreateDate: 2023-05-11 上午 11:20
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderBiz biz;

    @RequestMapping("addOrder")
    public Map<String,Object> addOrder(@RequestHeader String uid,HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        String cartgoods = request.getParameter("cartgoods");
        String ano = request.getParameter("ano");
        List<Map<String, Object>> lists = (List<Map<String, Object>>) new Gson().fromJson(cartgoods, List.class);

        Integer integer = this.biz.addOrder(lists, ano,uid);
        if(integer!=0){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }


    @RequestMapping("delorders")
    public Map<String,Object> delorders(@RequestHeader String uid,HttpServletRequest request, HttpServletResponse response) {

        String ono = request.getParameter("ono");
        Map<String,Object> map = new HashMap<>();
        Integer integer = this.biz.delorders(ono,uid);
        if(integer!=0){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }
    @RequestMapping("showOrderbyPage")
    public Map<String,Object> showOrderbyPage(@RequestHeader String uid,PageBean pageBean){
        Map<String,Object> map = new HashMap<>();
        PageBean bean = this.biz.showOrderbyPage(pageBean,uid);
        if(bean!=null){
            map.put("code",1);
            map.put("data",pageBean);
        }else {
            map.put("code",0);
        }
        return map;
    }
}
