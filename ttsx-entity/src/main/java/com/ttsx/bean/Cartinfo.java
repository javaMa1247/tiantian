package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:35
 * @Description:
 */
@Data
public class Cartinfo implements Serializable {
    private String cno;
    private String gno;
    private String mno;
    private int num;
}
