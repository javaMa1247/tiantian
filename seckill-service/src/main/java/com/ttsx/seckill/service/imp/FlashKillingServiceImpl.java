package com.ttsx.seckill.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttsx.bean.FlashKilling;
import com.ttsx.seckill.mapper.FlashKillingMapper;
import com.ttsx.seckill.service.FlashKillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-25 下午 6:55
 */
@Service
@Slf4j
public class FlashKillingServiceImpl extends ServiceImpl<FlashKillingMapper, FlashKilling> implements FlashKillingService {
    @Autowired
    private FlashKillingMapper flashKillingMapper;

    @Override
    public int decrByStock(Integer gno) {
        FlashKilling flashKilling = flashKillingMapper.selectOne(new QueryWrapper<FlashKilling>().lambda().eq(FlashKilling::getGno,gno));
        flashKilling.setCount(flashKilling.getCount()-1);
        int i = flashKillingMapper.updateById(flashKilling);
        if( i<= 0){
            throw new RuntimeException("减少库存失败");
        }
        return i;
    }

    @Override
    public List<FlashKilling> selectList() {
        return flashKillingMapper.selectList(new QueryWrapper<>());
    }
}
