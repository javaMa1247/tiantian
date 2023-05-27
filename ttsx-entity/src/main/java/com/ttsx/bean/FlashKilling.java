package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
    @TableId(type = IdType.AUTO,value = "fno")    //主键且自增
    private Integer fno;
    private Integer gno;
    @TableField("fk_price")
    private Double fkPrice;
    private Integer count;
    @TableField("start_data")
    private String start_data;
    private Integer time;

    public String getStart_data() {
        if (this.start_data == null){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            start_data = sdf.format(date);
        }
        return start_data;
    }

}
