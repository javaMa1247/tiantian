package com.ttsx.seckill.service;
import com.ttsx.seckill.entity.Order;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 2:59
 */
@Service
public interface OrderService {
    /**
     * 订单保存
     * @param order 实体
     */
    int saveOrder(Order order);

}
