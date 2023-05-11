package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:34
 * @Description:
 */
@Data

public class Memberinfo implements Serializable {
    @TableId(type = IdType.AUTO)    //主键且自增
    private Integer mno;
    @TableField("nickName")
    private String nickName;
    @TableField("realName")
    private String realName;
    private String pwd;
    private String tel;
    private String email;
    private String photo;
    @TableField("regDate")
    private String regDate;
    private Integer status;
    @TableField(exist = false)
    private boolean ban = false;
}
