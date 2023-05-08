package com.ttsx.order.biz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttsx.bean.Cartinfo;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.order.dao.CartDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:55
 */
@Service
public class CartBiz {
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private CartDao dao;
    public List<Cartinfo> showAllCart(){
        List<Cartinfo> cartinfos = this.dao.selectList(null);
        for (Cartinfo cartinfo : cartinfos) {
            cartinfo.setCount(cartinfo.getCount()+1);
            //TODO: 到nacos中查找res-foods服务中的   findById ，要得到菜品对象goods
            Map<String,Object> resultMap=this.feignApp.findById(   Integer.valueOf(cartinfo.getGno()) );
            Goodsinfo gs = null;
            if( "1".equalsIgnoreCase(  resultMap.get("code").toString())){
                Map m= (Map) resultMap.get("obj");
                //如何将一个Map转为  一个  Resfood对象  -> 反射.
                // spring的底层对json的处理使用 jackson框架，这个框架有将map转为对象的方法
                ObjectMapper mapper=new ObjectMapper();
                gs=mapper.convertValue(     m,  Goodsinfo.class );
                cartinfo.setGoodsinfo(gs);
            }

        }
        return cartinfos;
    }
}
