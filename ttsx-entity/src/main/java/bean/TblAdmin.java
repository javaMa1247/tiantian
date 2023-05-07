package bean;

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
    private String aid;
    private String aname;
    private String apwd;
}
