package com.ttsx.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:40
 * @Description:
 */
@Data
public class TblAdmin implements Serializable {
    @TableId(type = IdType.AUTO)    //主键且自增
    private String aid;
    private String aname;
    private String apwd;
}
