package com.ttsx.order.biz;

import com.ttsx.utils.BaseContext;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-12 下午 7:56
 */
public class Utils {
    public static Long MNO = 0L;

    public static Long getMNO() {
        if(BaseContext.getCurrentId()!=null){
            MNO = BaseContext.getCurrentId();
        }
        return MNO;
    }
}
