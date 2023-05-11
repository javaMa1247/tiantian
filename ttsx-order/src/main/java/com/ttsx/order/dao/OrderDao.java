package com.ttsx.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttsx.bean.OrderIteminfo;
import com.ttsx.bean.Orderinfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-11 上午 11:19
 */
public interface OrderDao extends BaseMapper<Orderinfo> {
    @Select("select ono,date_format(odate,'%Y-%m-%d %H:%I:%S') odate,orderinfo.ano,rdate,orderinfo.status,price,invoice  from addrinfo,orderinfo\n" +
            "                    where orderinfo.ano = addrinfo.ano and mno=#{mno} order by odate desc;")
    List<Orderinfo> selectAllOrder(@Param("mno") String mno);

    @Select("select orderiteminfo.ino ,ono,goodsinfo.*,nums,orderiteminfo.price smallCount from orderiteminfo,goodsinfo\n" +
            "where goodsinfo.gno=orderiteminfo.gno and ono=#{ono}")
    List<OrderIteminfo> selectOrderItemByOno(@Param("ono") String ono);

}
