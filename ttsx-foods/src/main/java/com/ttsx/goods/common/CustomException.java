package com.ttsx.goods.common;

/**
 * @Author: mqb
 * @Date: 2023/3/27
 * @Time: 16:11
 * @Description:
 */
//自定义业务异常类
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
