package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:23
 * @Description:
 */
@Data
public class Admininfo implements Serializable {
    private String tel;
    private String aid;
    private String aname;
    private String pwd;
}
