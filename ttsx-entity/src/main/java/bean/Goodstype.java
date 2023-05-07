package bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:28
 * @Description:
 */
@Data
public class Goodstype implements Serializable {
    private Integer tno;
    private String tname;
    private String pic;
    private String status;
}
