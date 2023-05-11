package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.bean.OrderIteminfo;
import com.ttsx.bean.Orderinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.order.dao.OrderDao;
import com.ttsx.order.dao.OrderItemDao;
import com.ttsx.utils.R;
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
    private FeignApp feignApp;

    private String mno = "3"; //TODO: mno
    public Integer addOrder(List<Map<String, Object>> orders,String ano){

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

    public List<Orderinfo> showOrderbyPage(){

        List<Orderinfo> list = this.dao.selectAllOrder(mno);
        if(list.size()<=0){
            return null;
        }
        for(Orderinfo order:list){
            String ono = order.getOno();
            List<OrderIteminfo> orderItemList = dao.selectOrderItemByOno(ono);
            for (OrderIteminfo iteminfo : orderItemList) {
                R<Goodsinfo> gno = this.feignApp.findById(Integer.valueOf(iteminfo.getGno()));
                iteminfo.setGoodsinfo(gno.getData());
            }
            order.setOrderItem(orderItemList);
        }
        if(list!=null&&list.size()>0){
            return list;
        }else{
           return null;
        }
    }
}
