package com.ttsx.seckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-25 下午 6:47
 */
@TableName("FlashKilling")
@Data
public class FlashKilling {
    @TableId(type = IdType.AUTO,value = "fno")
    private Integer fno;
    /**
     * 产品编号
     */
    @TableField("gno")
    private Integer gno;


    /**
     * 存货
     */
    @TableField("count")
    private Integer count;
}
