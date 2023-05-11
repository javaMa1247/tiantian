package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:38
 * @Description:
 */
@Data
@TableName("orderiteminfo")
public class OrderIteminfo implements Serializable {
    @TableId(value = "ino",type = IdType.AUTO)
    private String ino;
    private String ono;
    private String gno;
    private String nums;
    private String price;
    private String status;

    public String getStatus() {
        return "1";
    }

    @TableField(exist = false)
    private Goodsinfo goodsinfo;
    @TableField(exist = false)
    private int count;
    @TableField(exist = false)
    private Double smallCount;
}
