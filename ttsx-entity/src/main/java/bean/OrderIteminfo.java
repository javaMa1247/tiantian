package bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:38
 * @Description:
 */
@Data
public class OrderIteminfo implements Serializable {
    private String ino;
    private String ono;
    private String gno;
    private String nums;
    private String price;
    private String status;
}
