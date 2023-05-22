package com.ttsx.background.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ttsx.background.mapper.AdminMapper;
import com.ttsx.background.mapper.GoodsMapper;
import com.ttsx.background.mapper.OrderDao;
import com.ttsx.background.mapper.UserMapper;
import com.ttsx.bean.OrderInfoBeanX;
import com.ttsx.bean.OrderShowInfoBeanX;
import com.ttsx.bean.Orderinfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: tiantian
 * @description: 订单管理
 * @author: dx
 * @create: 2023/5/19 19:48
 */
@RestController
@Slf4j
@RequestMapping("/backgroud/order")
public class OrderMangeController {
    private int expireTime = 3600;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private OrderDao orderDao;


    //TODO:导入ttsx-order,  待解决导入问题 ：getOrderInfo，nextStatus
    //查询订单详情
    @RequestMapping("/getOrderInfo")
    public Map getOrderInfo() {
        Map map = new HashMap();
        try {
            List<OrderInfoBeanX> select = this.adminMapper.selectOrderInfo();
            for (OrderInfoBeanX orderInfoBeanX : select) {
                Integer status = orderInfoBeanX.getStatus();
                if (status == 1) {
                    orderInfoBeanX.setSstatus("已下单");
                } else if (status == 2) {
                    orderInfoBeanX.setSstatus("发货中");
                } else if (status == 3) {
                    orderInfoBeanX.setSstatus("送货中");
                } else {
                    orderInfoBeanX.setSstatus("已送达");
                }
                Integer invoice = orderInfoBeanX.getInvoice();
                if (invoice == 0) {
                    orderInfoBeanX.setSinvoice("无发票");
                } else if (invoice == 1) {
                    orderInfoBeanX.setSinvoice("电子发票");
                } else if (invoice == 2) {
                    orderInfoBeanX.setSinvoice("纸质发票");
                }
            }
            if (select.size() == 0) {
                throw new RuntimeException("无任何订单信息!");
            }
            map.put("code", 1);
            map.put("data", select);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    //下一步
    @RequestMapping("/nextStatus")
    public Map nextStatus(@RequestParam("ono") String ono) {
        Map map = new HashMap();
        try {
            Orderinfo orderinfo = this.orderDao.selectById(ono);
            Integer status = orderinfo.getStatus();
            String upSql = "update orderinfo set status = ? where ono = ? ";
            if (status != 1 && status != 2 && status != 3) {
                throw new RuntimeException("已经是最后一步!");
            }
            QueryWrapper<Orderinfo> qw1 = new QueryWrapper<>();
            qw1.eq("ono", ono);
            orderinfo.setStatus(status + 1);
            int i = this.orderDao.update(orderinfo, qw1);
            if (i == 0) {
                throw new RuntimeException("操作失败:操作数据为0");
            }
            map.put("code", 1);
            map.put("data", i);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    //获取订单详情
    @RequestMapping("/getOrderItem")
    public Map getOrderItem(@RequestParam("ono") String ono) {
        Map map = new HashMap();
        try {
            List<OrderShowInfoBeanX> select = this.adminMapper.selectOrderItem(ono);
            if (select.size() == 0) {
                throw new RuntimeException("订单详情遗失!请询问相关人员!");
            }
            String list = "";
            for (OrderShowInfoBeanX orderShowInfoBeanX : select) {
                list += orderShowInfoBeanX.toString();
            }
            map.put("code", 1);
            map.put("data", list);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 0);
            map.put("msg", e.getMessage());
        }
        return map;
    }
}
