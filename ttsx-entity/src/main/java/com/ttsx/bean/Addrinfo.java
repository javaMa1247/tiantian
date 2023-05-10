package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:20
 * @Description:
 */
@Data
public class Addrinfo implements Serializable {
    @TableId
    private String ano;
    private int mno;
    private String name;
    private String tel;
    private String province;
    private String city;
    private String area;
    private String addr;
    private int flag;
    private int status;
}
