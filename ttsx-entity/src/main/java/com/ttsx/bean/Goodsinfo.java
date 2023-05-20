package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:29
 * @Description:
 */
@Data
public class Goodsinfo implements Serializable {
    @TableId(type = IdType.AUTO)    //主键且自增
    private Integer gno;
    private Integer tno;
    private String gname;
    private Double price;
    private String intro;
    private Integer balance;
    private String pics;
    private String unit;
    private String qperied;
    @TableField(exist = false,select = false)
    private String tname;
    private String weight;
    private String descr;

}
