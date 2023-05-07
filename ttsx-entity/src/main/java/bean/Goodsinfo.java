package bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:29
 * @Description:
 */
@Data
public class Goodsinfo implements Serializable {
    private Integer gno;
    private Integer tno;
    private String gname;
    private Double price;
    private String intro;
    private Integer balance;
    private String pics;
    private String unit;
    private String qperied;
    private String tname;
    private String weight;
    private String descr;
}
