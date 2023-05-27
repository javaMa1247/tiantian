package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.OrderIteminfo;
import com.ttsx.bean.Orderinfo;
import com.ttsx.order.dao.OrderDao;
import com.ttsx.order.dao.OrderItemDao;
import com.ttsx.utils.PageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-11 上午 11:20
 */
@Service
@Slf4j
public class OrderBiz {
    @Autowired
    private OrderDao dao;

    @Autowired
    private OrderItemDao itemDao;

    @Autowired
    private OrderBizTmpl orderBizTmpl;

    public Integer addOrder(List<Map<String, Object>> orders,String ano,String mno){

//        int mno= user.getUserId();
        Integer res = 0;

        Orderinfo orderinfo = new Orderinfo();
        orderinfo.setAno(ano);
        orderinfo.setPrice(0.0);
        orderinfo.setInvoice(1);
        orderinfo.setStatus(1);
        int i = this.dao.insert(orderinfo);
        String ono = orderinfo.getOno();
        log.info(ono);
        OrderIteminfo orderIteminfo = new OrderIteminfo();
        for(Map<String, Object> list :orders){
            orderIteminfo.setGno((String) list.get("gno"));
            orderIteminfo.setNums(list.get("num").toString());
            orderIteminfo.setPrice(list.get("smallCount").toString());
            orderIteminfo.setOno(ono);
            this.itemDao.insert(orderIteminfo);
        }
        QueryWrapper<OrderIteminfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ono", ono);
        List<Object> result = itemDao.selectObjs(
                queryWrapper.select("SUM(price)")
        );
        if (result != null && !result.isEmpty()) {
            Object value = result.get(0);
            if (value instanceof BigDecimal) {
                BigDecimal sumPrice = (BigDecimal) value;
                // 使用 sumPrice 进行后续操作
                sumPrice = sumPrice.add(new BigDecimal(10));
                orderinfo.setPrice(sumPrice.doubleValue());
                dao.updateById(orderinfo);
                res = 1;
            }

        }

        return res;
    }

    public PageBean showOrderbyPage(PageBean pageBean,String mno){
//        int mno= user.getUserId();
        PageBean page = this.orderBizTmpl.findByPage(pageBean, mno+"");
        if(page!=null){
            return page;
        }else {
            return null;
        }

    }

    public Integer delorders(String ono, String uid) {
        QueryWrapper<OrderIteminfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ono", ono);
        int delete = itemDao.delete(queryWrapper);
        QueryWrapper<Orderinfo> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("ono", ono);
        int delete1 = dao.delete(queryWrapper1);
        if(delete>0 && delete1>0){
            return 1;
        }
        return 0;
    }
}
