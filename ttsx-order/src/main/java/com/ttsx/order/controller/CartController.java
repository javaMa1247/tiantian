package com.ttsx.order.controller;

import com.ttsx.bean.Cartinfo;
import com.ttsx.order.biz.CartBiz;
import com.ttsx.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:14
 */
@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private CartBiz biz;
    @RequestMapping("showAllcartInfo")
    public List<Cartinfo> showAllcartInfo(){

        String sql = "select cno,mno,num,goodsinfo.*,goodsinfo.price*num as smallCount " +
                "from goodsinfo,cartinfo where goodsinfo.gno=cartinfo.gno and mno= ? ";

        List<Cartinfo> cartinfos = this.biz.showAllCart();
        return cartinfos;
    }
}
