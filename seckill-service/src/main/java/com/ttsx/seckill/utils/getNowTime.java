package com.ttsx.seckill.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-27 下午 5:05
 */
public class getNowTime {
    public static String getTime(){
        Date now = new Date();
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 现在时间
        return  sdf.format(now);
    }

}
