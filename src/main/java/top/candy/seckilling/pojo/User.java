package top.candy.seckilling.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String logo;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCounts;
}
