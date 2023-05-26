package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: -
 * @description:  秒杀商品 与 商品的 集合类
 * @author: dx
 * @create: 2023/5/24 10:22
 */
@Data
public class FlashKillingVO implements Serializable {
    private Integer fno;
    private Integer gno;
    private Integer tno;
    private Double price;
    private Double fk_price;
    private Integer count;
    private Integer currentCount;
    private Date start_data;
    private String start_dateString;
    private Integer time;
    private String gname;
    private String intro;
    private String pics;
    private String unit;
    private String qperied;
    private String weight;


    public Integer getCurrentCount() {
        if (this.currentCount==null){
            this.currentCount = this.getCount();
        }
        return currentCount;
    }

    public String getStart_dateString() {
        if (this.start_dateString == null){
            long timestamp = this.getStart_data().getTime();
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            start_dateString = sdf.format(date);
        }
        return start_dateString;
    }
}
