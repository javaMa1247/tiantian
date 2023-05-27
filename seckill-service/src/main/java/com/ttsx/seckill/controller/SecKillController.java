package com.ttsx.seckill.controller;


import com.ttsx.seckill.service.imp.MqStockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    /**
     * 使用redis+消息队列进行秒杀实现
     * @param userName 用户名称
     * @param gno 商品id
     * @return String
     */
    @PostMapping(value = "/secKill")
//    @ApiOperation(value = "redis+消息队列进行秒杀实现", notes = "redis+消息队列进行秒杀实现")
//    @LimitNumber(value = 2)
//    @AccessLimit(seconds = 1,maxCount = 800)
    public String secKill(@RequestParam(value = "userName") String userName, @RequestParam(value = "gno") Integer gno) {
        return mqStockService.secKill(userName, gno);
    }


}
