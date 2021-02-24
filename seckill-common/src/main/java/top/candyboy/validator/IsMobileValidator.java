package top.candyboy.validator;

import org.apache.commons.lang3.StringUtils;
import top.candyboy.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    //初始化方法，拿到我们自定义的注解
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    //进行校验的方法
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //如果这个值是必须的，那么要判断格式
        if (required) {
            return ValidatorUtil.isMobile(s);
        } else {
            if (StringUtils.isEmpty(s)) {
                return true;
            } else {
                /*
                Field error in object 'loginVo' on field 'mobile': rejected value [23312312312]; codes [IsMobile.loginVo.mobile,IsMobile.mobile,IsMobile.java.lang.String,IsMobile]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loginVo.mobile,mobile]; arguments []; default message [mobile],true]; default message [手机号码格式错误]]
                 */
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
