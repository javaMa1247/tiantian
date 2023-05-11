package com.ttsx.feignApi;

import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.config.FeignLogConfig;
import com.ttsx.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 3:02
 */
@FeignClient(value = "ttsx-foods",path = "goods")
public interface FeignApp {

    //open feign 支持SpringMVC 注解
    @RequestMapping("findById/{fid}")
    public R<Goodsinfo> findById(@PathVariable Integer fid);
//
//    @RequestMapping("findAll")
//    public Map<String,Object> findAll();
//
//    @RequestMapping("findByPage")
//    public Map<String,Object> findByPage(@RequestParam int pageno, @RequestParam int pagesize, @RequestParam String sortby,
//                                         @RequestParam String sort);
    /**
     * 将以上接口通过openFeign 的动态代理机制生成一个实现类
     *   String url = http://value+path+RequestMapping
     */
}



