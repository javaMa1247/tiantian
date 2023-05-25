package com.ttsx.seckill.entity;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-23 下午 3:00
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 商品库存表
 */
@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("gno")
    private Integer gno;


    @TableField("order_user")
    private String orderUser;


    @TableField("create_by")
    private String createBy;


    @TableField("update_by")
    private String updateBy;


    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableField("del_flag")
    private String delFlag;
}
