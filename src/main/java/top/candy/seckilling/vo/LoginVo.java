package top.candy.seckilling.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import top.candy.seckilling.validator.IsMobile;

import javax.validation.constraints.NotNull;

@Component
@Data
public class LoginVo {

    @NotNull
    //自定义一个validator
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
