package top.candyboy.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class UserVo {
    //@Id
    private Long id;
    private String username;
    //@JsonIgnore
    private String password;
    private String nickname;
    //@JsonIgnore
    private String realname;
    private String face;
    private String mobile;
    private String email;
    private Integer sex;
    private Date birthday;
    //@Column(name = "created_time")
    private Date createdTime;
    //@Column(name = "updated_time")
    private Date updatedTime;
    // 用户会话token
    private String userUniqueToken;
}
