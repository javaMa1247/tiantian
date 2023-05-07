package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:36
 * @Description:
 */
@Data
public class Orderinfo implements Serializable {
    private String ono;
    private String odate;
    private String ano;
    private String sdate;
    private String rdate;
    private int status;
    private Double price;
    private int invoice;

}
