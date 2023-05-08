package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:35
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cartinfo implements Serializable {
    private String cno;
    private String gno;
    private String mno;
    private int num;

    @TableField(exist = false)    //注解，表示该属性不对应数据库表中的任何列，而是与其他实体类进行关联。
    private Goodsinfo goodsinfo;

    @TableField(exist = false)
    private int count;

    public Cartinfo(String cno, String gno, String mno, int num) {
        this.cno = cno;
        this.gno = gno;
        this.mno = mno;
        this.num = num;
    }

    public int getCount() {
        return this.num;
    }

    @TableField(select = false)
    private Double smallCount;

}
