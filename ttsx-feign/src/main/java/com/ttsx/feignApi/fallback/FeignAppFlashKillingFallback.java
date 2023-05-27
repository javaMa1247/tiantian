package com.ttsx.feignApi.fallback;

import com.ttsx.bean.FlashKillingVO;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.utils.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/25 9:53
 */
@Component
public class FeignAppFlashKillingFallback implements FeignAppFlashKilling {
    @Override
    public R<FlashKillingVO> showmsGoodsDetail(String time, String fno) {
        //返回兜底数据
        return null;
    }

    @Override
    public R<List<FlashKillingVO>> selectmsGoodsInfo(Object time) {
        return null;
    }

    @Override
    public R<List<FlashKillingVO>> selectmsGoodsInfoAll() {
        return null;
    }

}
