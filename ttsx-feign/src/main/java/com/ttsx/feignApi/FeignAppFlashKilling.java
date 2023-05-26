package com.ttsx.feignApi;


import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.feignApi.fallback.FeignAppFlashKillingFallback;
import com.ttsx.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "ttsx-seckill",path = "fkFegin",fallback = FeignAppFlashKillingFallback.class)
public interface FeignAppFlashKilling {
    //展示秒杀商品详情
    @GetMapping("/showmsGoodsDetail")
    public R<FlashKillingVO> showmsGoodsDetail(@RequestParam(value = "time",required = false)  String time,
                                               @RequestParam("seckillId")  String fno);
    @GetMapping("/showmsGoodsInfo")
    public R<List<FlashKillingVO>> selectmsGoodsInfo(@RequestParam(value = "time")  Object time );

    @GetMapping("/showmsGoodsInfoAll")
    public R<List<FlashKillingVO>> selectmsGoodsInfoAll();
}

