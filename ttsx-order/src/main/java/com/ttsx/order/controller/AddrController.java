package com.ttsx.order.controller;

import com.ttsx.bean.Addrinfo;
import com.ttsx.order.biz.AddrBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-10 下午 6:52
 */
@RestController
@RequestMapping("addr")
public class AddrController {
    @Autowired
    private AddrBiz biz;
    @RequestMapping("showAddr")
    public Map<String,Object> showAddr (@RequestHeader String uid){
        Map<String,Object> map = new HashMap<>();
        List<Addrinfo> list= this.biz.showAddr(uid);
        if(list.size()>0){
            map.put("code",1);
            map.put("data",list);
        }else {
            map.put("code",0);

        }
        return map;
    }

    @RequestMapping("addAddr")
    public Map<String,Object> addAddr(@RequestHeader String uid,Addrinfo addrinfo){
        Map<String,Object> map = new HashMap<>();
        Integer result = this.biz.addAddr(addrinfo,uid);
        if(result!=0){
            map.put("code",1);
        }else {
            map.put("code",0);
        }
        return map;
    }

    @RequestMapping("updateAddr")
    public Map<String,Object> updateAddr(@RequestHeader String uid,Addrinfo addrinfo){
        Map<String,Object> map = new HashMap<>();
        Integer result = this.biz.updateAddr(addrinfo,uid);
        if(result!=0){
            map.put("code",1);
        }else {
            map.put("code",0);
        }
        return map;
    }

    @RequestMapping("showAddrinfo")
    public Map<String,Object> showAddrinfo(@RequestHeader String uid,Addrinfo addrinfo){
        Map<String,Object> map = new HashMap<>();
        map = this.biz.showAddrinfo(addrinfo,uid);
        return map;
    }

}
