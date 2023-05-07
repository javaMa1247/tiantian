package bean;

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
    private Integer mno;
    private String nickName;
    private String realName;
    private String pwd;
    private String tel;
    private String email;
    private String photo;
    private String regDate;
    private Integer status;
    private boolean ban = false;
}
