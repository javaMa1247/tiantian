package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/23 21:23
 */
@Data
@TableName(value = "FlashKilling")
public class FlashKilling implements Serializable {
    @TableId(type = IdType.AUTO)    //主键且自增
    private Integer fno;
    private Integer gno;
    private Double fk_price;
    private Integer count;
    private Date start_data;
    private Integer time;
}
