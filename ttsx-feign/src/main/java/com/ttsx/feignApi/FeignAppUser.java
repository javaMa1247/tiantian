package com.ttsx.feignApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-13 下午 9:54
 */
@FeignClient(value = "ttsx-user",path = "user")
public interface FeignAppUser {

    @GetMapping("getUserId")
    public Integer getUserId();
}
