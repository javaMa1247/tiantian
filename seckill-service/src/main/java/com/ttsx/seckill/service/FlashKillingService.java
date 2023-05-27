package com.ttsx.seckill.service;

import com.ttsx.bean.FlashKilling;

import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-25 下午 6:52
 */

public interface FlashKillingService {
    /**
     * 秒杀商品后-减少库存
     * @param gno 商品编号
     */
    int decrByStock(Integer gno);


    /**
     * 秒杀商品列表
     * @return List<FlashKilling>
     */
    List<FlashKilling> selectList();
}
