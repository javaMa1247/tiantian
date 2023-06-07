package com.ttsx.seckill.controller;


import com.ttsx.bean.FlashKilling;
import com.ttsx.bean.FlashKillingVO;
import com.ttsx.feignApi.FeignAppFlashKilling;
import com.ttsx.seckill.service.imp.MqStockServiceImpl;
import com.ttsx.seckill.utils.JWTUtils;
import com.ttsx.seckill.utils.getNowTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-24 下午 6:40
 */
@RestController
@Slf4j
//@Api(value = "SecKillController",  tags = "秒杀控制层")
@RequestMapping("/seck")
public class SecKillController {

    @Autowired
    private MqStockServiceImpl mqStockService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FeignAppFlashKilling feignAppFlashKilling;

    //下单操作

    /**
     * 使用redis+消息队列进行秒杀实现
     * @param time 场次
     * @param fno 商品id
     * @return String
     */
    @RequestMapping("/doSeckill")
//    @ApiOperation(value = "redis+消息队列进行秒杀实现", notes = "redis+消息队列进行秒杀实现")
//    @LimitNumber(value = 2)
//    @AccessLimit(seconds = 1,maxCount = 800)
    public Map<String ,Object> secKill(@RequestParam("time")  String time,
                                       @RequestParam("seckillId")  String fno,
                                       @RequestHeader(required = false) String uid,
                                        HttpServletRequest request) {
        if(Objects.isNull(uid)&&Objects.nonNull(request.getHeader("token")) ){
            String token = request.getHeader("token");
            uid = (String) JWTUtils.getTokenInfo(token).get("userid");
        }
//        String
//        FlashKillingVO flashKillingVO = this.feignAppFlashKilling.showmsGoodsDetail(time, fno).getData();

//        //判断时间
//        if(now.getTime() < flashKillingVO.getStart_data().getTime()){
//            map.put("code",-1);
//            return map;
//        }
        Map<String ,Object> map = new HashMap<>();
        String nowTime = getNowTime.getTime();
        FlashKillingVO vo = (FlashKillingVO) this.redisTemplate.opsForHash().get(nowTime + "\t" + time, String.valueOf(fno));
        if (vo == null){
            map.put("code",0);
            return map;
        }
        return mqStockService.secKill(vo.getGno(), uid,time);
    }


}
