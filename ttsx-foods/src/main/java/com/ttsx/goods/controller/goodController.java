package com.ttsx.goods.controller;

import com.ttsx.bean.Goodsinfo;
import com.ttsx.goods.biz.FoodsBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 3:13
 */
@RestController
@RequestMapping("goods")
@Slf4j
public class goodController {

    @Autowired
    private FoodsBiz biz;
    @RequestMapping("findById/{fid}")
    public Map<String,Object> findById(@PathVariable Integer fid){
        log.info("进来了");
        Map<String,Object> map = new HashMap<>();
        Goodsinfo good = null;
        try{
            good = this.biz.findById(fid);
        }catch (Exception e){
            map.put("code",0);
            map.put("obj",e.getMessage());
            return map;
        }
        map.put("code",1);
        map.put("obj",good);
        //map.put("ms",ms);
        return map;
        //return this.resfoodBiz.findById(fid);
    }
}
