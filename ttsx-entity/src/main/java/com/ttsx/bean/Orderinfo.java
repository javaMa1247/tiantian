package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:36
 * @Description:
 */
@Data
public class Orderinfo implements Serializable {
    @TableId(value = "ono",type = IdType.AUTO)
    private String ono;
    private String odate;
    private String ano;
    private String sdate;
    private String rdate;
    private int status;
    private Double price;
    private int invoice;

    @TableField(exist = false,select = false)
    private String mno;
    @TableField(exist = false,select = false)
    private List<OrderIteminfo> orderItem;

    public String getOdate() {
        if(odate==null|| "".equals(odate)){
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss");
            this.odate = simpleDateFormat.format(date);
        }
        return odate;
    }

    public String getSdate() {
        return this.odate;
    }

    public String getRdate() {
        return this.odate;
    }
}
