package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:28
 * @Description:
 */
@Data
public class Goodstype implements Serializable {
    @TableId(type = IdType.AUTO)    //主键且自增
    private Integer tno;
    private String tname;
    private String pic;
    private String status;
}
