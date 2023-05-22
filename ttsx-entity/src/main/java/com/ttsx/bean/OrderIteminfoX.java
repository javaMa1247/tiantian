package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/13 15:34
 */
@Data
public class OrderIteminfoX implements Serializable {
    private Integer value;
    private String name;

    public void setValue(String value) {
        int val = Integer.parseInt(value);
        this.value = (Integer) val;
    }

}
