package com.ttsx.seckill.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.seckill.entity.Order;
import com.ttsx.seckill.mapper.OrderMapper;
import com.ttsx.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:06
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Override
    public int saveOrder(Order order) {
        int i = orderMapper.insert(order);
        if (i <= 0) {
            throw new RuntimeException("保存订单失败");
        }
        return i;
    }
}
