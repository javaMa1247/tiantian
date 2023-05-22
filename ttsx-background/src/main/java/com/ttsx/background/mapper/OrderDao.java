package com.ttsx.background.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttsx.bean.OrderIteminfo;
import com.ttsx.bean.Orderinfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-11 上午 11:19
 */
@Component
public interface OrderDao extends BaseMapper<Orderinfo> {
    @Select("select orderiteminfo.ino ,ono,goodsinfo.*,nums,orderiteminfo.price smallCount from orderiteminfo,goodsinfo\n" +
            "where goodsinfo.gno=orderiteminfo.gno and ono=#{ono}")
    List<OrderIteminfo> selectOrderItemByOno(@Param("ono") String ono);

    @Select("select ono,date_format(odate,'%Y-%m-%d %H:%I:%S') odate,orderinfo.ano,rdate,orderinfo.status,price,invoice  from addrinfo,orderinfo\n" +
            "where orderinfo.ano = addrinfo.ano and mno= #{mno} order by ono desc limit #{start}, #{pagesize}")
    List<Orderinfo> selectAllOrderByPage(@Param("mno") String mno, @Param("start") Integer start,
                                         @Param("pagesize") Integer pagesize);

//    IPage<Orderinfo> selectAllOrderByPage(Page<Orderinfo> page, @Param("mno") String mno);

    @Select("SELECT COUNT(*) " +
            "FROM orderinfo o " +
            "INNER JOIN addrinfo a ON (o.ano = a.ano) " +
            "WHERE a.mno = #{mno}")
    Integer getOrderInfoCountByMno(@Param("mno") String mno);

}
