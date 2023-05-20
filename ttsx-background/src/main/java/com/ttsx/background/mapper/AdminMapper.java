package com.ttsx.background.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttsx.bean.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AdminMapper  extends BaseMapper<TblAdmin> {
    //订单列表
    @Select("select ono,odate,o.status,price,invoice,addr from orderinfo o inner join addrinfo a on (o.ano = a.ano) where o.status = 1 or o.status = 2 or o.status = 3 or o.status = 4 order by ono desc")
    List<OrderInfoBeanX> selectOrderInfo();
    //订单详情
    @Select("select g.gname,o.nums,o.price from orderiteminfo o inner join goodsinfo g on (o.gno = g.gno) where ono = #{ono} ")
    List<OrderShowInfoBeanX> selectOrderItem(@Param("ono") String ono);
    //商品列表
    @Select( "select gno,gname,price,intro,balance,pics,unit,qperied,weight,tname,g1.tno,descr from goodsinfo g1 inner join goodstype g2 on (g1.tno = g2.tno) where 1 = 1 order by gno asc")
    List<Goodsinfo> selectGoodsInfo();
    //商品详情
    @Select("select tno,tname from goodstype where 1 = 1 ")
    List<Goodsinfo> selectGoodsData();
    //echarts统计数据
    @Select("select sum(nums) as value,tname as name from orderiteminfo,goodsinfo,goodstype where goodsinfo.gno=orderiteminfo.gno and goodsinfo.tno=goodstype.tno group by tname ")
    List<OrderIteminfoX> selectOrderiteminfo();

}
